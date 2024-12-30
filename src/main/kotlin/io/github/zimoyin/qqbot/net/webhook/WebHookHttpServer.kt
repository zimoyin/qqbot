package io.github.zimoyin.qqbot.net.webhook

import io.github.zimoyin.qqbot.LocalLogger
import io.github.zimoyin.qqbot.bot.Bot
import io.github.zimoyin.qqbot.event.events.platform.bot.BotOfflineEvent
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
    val webSocketServerTcpSocketList = mutableListOf<ServerWebSocket>()

    var isStarted = false
        private set

    private fun init() {
        payloadCmdHandler = PayloadCmdHandler(bot, vertx)
        router = Router.router(vertx)
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
                        if (payload.opcode != 13) webSocketServerTcpSocketList.forEach {
                            it.writeTextMessage(payload.toJsonString())
                        }
                    }
                    payloadCmdHandler.handle(request.headers(), payload, response)
                }.onFailure {
                    logger.error("WebHook 处理事件发生异常 [path: /]: ${body.writeToText()}", it)
                }
                response.end()
            }
        }

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
        payloadCmdHandler.close()
        return webHttpServer.close()
    }

    private fun HttpServer.addWebSocketForwarding(): HttpServer {
        return WebSocketServerHandler(this@WebHookHttpServer).addWebSocketForwarding(this)
    }
}
