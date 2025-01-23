package io.github.zimoyin.qqbot.net.webhook

import io.github.zimoyin.qqbot.LocalLogger
import io.github.zimoyin.qqbot.bot.Bot
import io.github.zimoyin.qqbot.event.events.platform.bot.BotOfflineEvent
import io.github.zimoyin.qqbot.net.Intents
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.net.http.TencentOpenApiHttpClient
import io.github.zimoyin.qqbot.net.webhook.handler.PayloadCmdHandler
import io.github.zimoyin.qqbot.net.webhook.handler.WebSocketServerHandler
import io.github.zimoyin.qqbot.utils.ex.isInitialStage
import io.github.zimoyin.qqbot.utils.ex.mapTo
import io.github.zimoyin.qqbot.utils.ex.writeToText
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.http.HttpServer
import io.vertx.core.http.ServerWebSocket
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.coroutines.CoroutineVerticle

/**
 *
 * @author : zimo
 * @date : 2024/12/21
 */
class WebHookHttpServer(
    private val promise: Promise<WebHookHttpServer>,
    val bot: Bot,
    @get:JvmName("getConfig")
    val webHookConfig: WebHookConfig,
) : CoroutineVerticle() {
    private val debugLog = bot.context.getBoolean("PAYLOAD_CMD_HANDLER_DEBUG_LOG") ?: false
    private val logger = LocalLogger(WebHookHttpServer::class.java)
    private lateinit var payloadCmdHandler: PayloadCmdHandler

    lateinit var router: Router
        private set

    @get:JvmName("getServer")
    lateinit var webHttpServer: HttpServer
        private set

    @get:JvmName("getServerWebSockets")
    val webSocketServerInfo = WebSocketServerInfo().apply { config = webHookConfig;debugLog0 = debugLog }

    var isStarted = false
        private set

    private fun init() {
        payloadCmdHandler = PayloadCmdHandler(bot, vertx)
        router = Router.router(vertx)
        // 处理腾讯服务器访问
        router.route("/").handler {
            val request = it.request()
            val response = it.response()

            response.setChunked(true)
            request.bodyHandler { body ->
                if (body.writeToText().isEmpty()) {
                    response.end(jsonObjectOf("code" to 400, "message" to "request body is empty").toString())
                    return@bodyHandler
                }
                kotlin.runCatching {
                    val payload = kotlin
                        .runCatching { body.mapTo(Payload::class.java) }
                        .onFailure { logger.error("WebHook 处理事件发生异常 [path: /] 无法将 Json 解析为 Payload: ${body.writeToText()}") }
                        .getOrNull() ?: return@runCatching
                    payload.metadata = body.writeToText()
                    if (webHookConfig.enableWebSocketForwarding) {
                        if (payload.opcode != 13) webSocketServerInfo.sendPayloadToWsClient(payload)
                    }
                    payloadCmdHandler.handle(request.headers(), payload, response)
                }.onFailure {
                    logger.error("WebHook 处理事件发生异常 [path: /]: ${body.writeToText()}", it)
                }
                response.end()
            }
        }

        // openapi 转发
        if (webHookConfig.enableWebSocketForwarding) router.route("/*").handler {
            val request = it.request()
            val response = it.response()
            response.setChunked(true)

            request.bodyHandler { body ->
                if (request.getHeader("Authorization") == null) {
                    response.end(
                        jsonObjectOf(
                            "code" to 401,
                            "method" to request.method().name(),
                            "path" to request.path(),
                            "message" to "Authorization is required"
                        ).toString()
                    )
                    return@bodyHandler
                }
                TencentOpenApiHttpClient
                    .client
                    .request(request.method(), request.path())
                    .putHeaders(request.headers())
                    .sendBuffer(body)
                    .onSuccess { res ->
                        for (header in res.headers()) {
                            response.putHeader(header.key, header.value)
                        }
                        response.statusCode = res.statusCode()
                        response.statusMessage = res.statusMessage()
                        response.end(res.body()).onSuccess {
                            if (debugLog) logger.debug(
                                "转发服务器openapi完成: [${request.path()}] [${res.statusCode()}:${res.statusMessage()}] : ${
                                    res.body().writeToText()
                                }"
                            )
                        }.onFailure {
                            logger.error(
                                "转发信息回客户端出现异常: [${request.method()}] ${request.path()} \n headers: ${request.headers()} \n body: ${body.writeToText()}",
                                it
                            )
                        }
                    }.onFailure {
                        response.statusCode = 502
                        response.end(jsonObjectOf("code" to 502, "message" to "转发服务器出现异常").toString())
                        logger.error(
                            "转发服务器出现异常: [${request.method()}] ${request.path()} \n headers: ${request.headers()} \n body: ${body.writeToText()}",
                            it
                        )
                    }
            }
        }
    }


    override suspend fun start() {
        if (isStarted) throw IllegalStateException("already started")
        kotlin.runCatching {
            init()
            webHttpServer = vertx.createHttpServer(webHookConfig.options)
            if (debugLog) logger.info("WebHookHttpServer 创建成功")
            webHttpServer.addWebSocketForwarding()
                .requestHandler(router)
                .listen(webHookConfig.port, webHookConfig.host)
                .onSuccess {
                    if (promise.isInitialStage()) logger.info("WebHookHttpServer启动成功: ${webHookConfig.host}:${it.actualPort()}")
                    promise.tryComplete(this)
                    isStarted = true
                }.onFailure {
                    if (promise.isInitialStage()) logger.error("WebHookHttpServer启动失败", it)
                    promise.tryFail(it)
                    close()
                }
        }.onFailure {
            if (promise.isInitialStage()) logger.error("WebHookHttpServer启动失败", it)
            promise.tryFail(it)
            close()
        }
    }

    fun addRouter(var1: Handler<RoutingContext>) {
        router.route().handler(var1)
    }

    fun clearRouter() {
        router.clear()
    }

    val port by lazy { webHttpServer.actualPort() }
    val host by lazy { webHookConfig.host }

    fun close(): Future<Void> {
        isStarted = false
        bot.config.botEventBus.broadcastAuto(
            BotOfflineEvent(
                botInfo = bot.botInfo,
                throwable = null
            )
        )
        webSocketServerInfo.closeWebsocketServerTcpSocket()
        webSocketServerInfo.clearWebSocketServerTcpSocketList()
        kotlin.runCatching { payloadCmdHandler.close() }
        return webHttpServer.close()
    }


    private fun HttpServer.addWebSocketForwarding(): HttpServer {
        return WebSocketServerHandler(this@WebHookHttpServer).addWebSocketForwarding(this)
    }

    override suspend fun stop() {
        close()
    }

    class WebSocketServerInfo {
        lateinit var config: WebHookConfig
        var debugLog0: Boolean = false
        lateinit var server: WebHookHttpServer
        lateinit var webSocketServerHandler: WebSocketServerHandler
        val webSocketServerTcpSocketList: MutableList<ServerWebSocket> = mutableListOf<ServerWebSocket>()
        val webSocketServerTcpSocketMap = mutableMapOf<ServerWebSocket, Int>()

        private val logger = LocalLogger(WebSocketServerInfo::class.java)
        fun closeWebsocketServerTcpSocket() {
            runCatching {
                webSocketServerTcpSocketList.forEach {
                    runCatching { it.reject() }
                }
            }
        }

        fun clearWebSocketServerTcpSocketList() {
            runCatching { webSocketServerTcpSocketList.clear() }
        }

        fun sendPayloadToWsClient(payload: Payload) {
            if (debugLog0) logger.debug("WebSocketServer 准备发送消息: $payload")
            webSocketServerTcpSocketList.forEach {
                if (debugLog0) logger.debug("WebSocketServer 准备发送消息到[${it.remoteAddress()}]: $payload")
                if (config.enableWebSocketForwardingIntentsVerify) {
                    val eventIntent = Intents.entries.firstOrNull {
                        transformTo((payload.eventType ?: "Not Found Event Type")).contains(it.name, true)
                    }
                    val intent = webSocketServerTcpSocketMap[it]
                    if (intent != null) {
                        val intentsSet = Intents.decodeIntents(intent)
                        if (debugLog0) logger.debug("WebSocketServer 检测[${it.remoteAddress()}]是否订阅了${eventIntent?.name}事件")
                        if (debugLog0) logger.debug("WebSocketServer [${it.remoteAddress()}]订阅的事件列表：${intentsSet.map { it.name }}")
                        if (debugLog0) logger.debug(
                            "WebSocketServer [${it.remoteAddress()}]检测结果,是否订阅了事件：${
                                intentsSet.contains(
                                    eventIntent
                                )
                            }"
                        )
                        if (!intentsSet.contains(eventIntent)) {
                            if (debugLog0) logger.debug("WebSocketServer [${it.remoteAddress()}]未订阅事件，已忽略发送")
                            return@forEach
                        }
                    }
                }
                kotlin.runCatching {
                    it.writeTextMessage(payload.toJsonString())
                }.onFailure {
                    logger.debug("WebSocketServer 发送消息失败", it)
                }
            }
            if (debugLog0) logger.debug("WebSocketServer 发送信息结束")
        }

        // 转换
        private fun transformTo(eventType: String): String {
            return when (eventType) {
                "GUILD_MESSAGES", "MESSAGE_CREATE", "MESSAGE_DELETE" -> Intents.GUILD_MESSAGES.name
                "PUBLIC_GUILD_MESSAGES", "AT_MESSAGE_CREATE", "PUBLIC_MESSAGE_DELETE" -> Intents.PUBLIC_GUILD_MESSAGES.name
                "AUDIO_ACTION", "AUDIO_START", "AUDIO_FINISH", "AUDIO_ON_MIC", "AUDIO_OFF_MIC" -> Intents.AUDIO_ACTION.name
                "FORUMS_EVENT", "FORUM_THREAD_CREATE", "FORUM_THREAD_UPDATE", "FORUM_THREAD_DELETE", "FORUM_POST_CREATE", "FORUM_POST_DELETE", "FORUM_REPLY_CREATE", "FORUM_REPLY_DELETE", "FORUM_PUBLISH_AUDIT_RESULT" -> Intents.FORUMS_EVENT.name
                "MESSAGE_AUDIT", "MESSAGE_AUDIT_PASS", "MESSAGE_AUDIT_REJECT" -> Intents.MESSAGE_AUDIT.name
                "INTERACTION", "INTERACTION_CREATE" -> Intents.INTERACTION.name
                "GROUP_INTENTS", "GROUP_AND_C2C_EVENT" -> Intents.GROUP_INTENTS.name
                "AUDIO_OR_LIVE_CHANNEL_MEMBER", "AUDIO_OR_LIVE_CHANNEL_MEMBER_ENTER", "AUDIO_OR_LIVE_CHANNEL_MEMBER_EXIT" -> Intents.AUDIO_OR_LIVE_CHANNEL_MEMBER.name
                "OPEN_FORUMS_EVENT", "OPEN_FORUM_THREAD_CREATE", "OPEN_FORUM_THREAD_UPDATE", "OPEN_FORUM_THREAD_DELETE", "OPEN_FORUM_POST_CREATE", "OPEN_FORUM_POST_DELETE", "OPEN_FORUM_REPLY_CREATE", "OPEN_FORUM_REPLY_DELETE" -> Intents.OPEN_FORUMS_EVENT.name
                "DIRECT_MESSAGE", "DIRECT_MESSAGE_CREATE", "DIRECT_MESSAGE_DELETE" -> Intents.DIRECT_MESSAGE.name
                "GUILD_MESSAGE_REACTIONS", "MESSAGE_REACTION_ADD", "MESSAGE_REACTION_REMOVE" -> Intents.GUILD_MESSAGE_REACTIONS.name
                "GUILD_MEMBERS", "GUILD_MEMBER_ADD", "GUILD_MEMBER_UPDATE", "GUILD_MEMBER_REMOVE" -> Intents.GUILD_MEMBERS.name
                "GUILDS", "GUILD_CREATE", "GUILD_UPDATE", "GUILD_DELETE" -> Intents.GUILDS.name
                else -> eventType
            }
        }
    }


}
