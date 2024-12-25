package io.github.zimoyin.qqbot.net.webhook

import io.github.zimoyin.qqbot.LocalLogger
import io.github.zimoyin.qqbot.bot.Bot
import io.github.zimoyin.qqbot.event.events.platform.bot.BotOfflineEvent
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.net.webhook.handler.PayloadCmdHandler
import io.github.zimoyin.qqbot.utils.cpu
import io.github.zimoyin.qqbot.utils.ex.*
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.http.HttpServer
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.codec.BodyCodec.jsonObject
import io.vertx.ext.web.handler.sockjs.SockJSHandler
import io.vertx.ext.web.handler.sockjs.SockJSSocket
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.intellij.lang.annotations.Language
import java.util.*

/**
 *
 * @author : zimo
 * @date : 2024/12/21
 */
class WebHookHttpServer(
    private val promise: Promise<WebHookHttpServer>,
    val bot: Bot,
    val webHookConfig: WebHookConfig,
) : CoroutineVerticle() {
    lateinit var router: Router
        private set
    lateinit var webHttpServer: HttpServer
        private set
    private val logger = LocalLogger(WebHookHttpServer::class.java)
    private val payloadCmdHandler: PayloadCmdHandler = PayloadCmdHandler(bot)
    private val wsList = mutableListOf<ServerWebSocket>()

    fun init() {
        router = Router.router(vertx)
        router.route("/").handler {
            val request = it.request()
            val response = it.response()

            response.setChunked(true)
            request.bodyHandler { body ->
                kotlin.runCatching {
                    val payload = body.mapTo(Payload::class.java)
                    payload.metadata = body.writeToText()
                    if (webHookConfig.enableWebSocketForwarding) {
                        if (payload.opcode != 13) wsList.forEach {
                            it.writeTextMessage(payload.toJsonString())
                        }
                    }
                    payloadCmdHandler.handle(request.headers(), payload, response)
                }.onFailure {
                    logger.error("WebHook 处理事件发生异常: ${body.writeToText()}", it)
                }
                response.end()
            }
        }
    }

    override suspend fun start() {
        kotlin.runCatching {
            init()
            vertx.createHttpServer(webHookConfig.options)
                .addWebSocketForwarding()
                .requestHandler(router)
                .listen(webHookConfig.port, webHookConfig.host)
                .onSuccess {
                    if (promise.isInitialStage()) logger.info("WebHookHttpServer启动成功: ${webHookConfig.host}:${webHookConfig.port}")
                    promise.tryComplete(this)
                    webHttpServer = it
                }.onFailure {
                    if (promise.isInitialStage()) logger.error("WebHookHttpServer启动失败", it)
                    promise.tryFail(it)
                }
        }.onFailure {
            if (promise.isInitialStage()) logger.error("WebHookHttpServer启动失败", it)
            promise.tryFail(it)
        }
    }

    fun addRouter(var1: Handler<RoutingContext>) {
        router.route().handler(var1)
    }

    fun clearRouter() {
        router.clear()
    }

    fun close() {
        bot.config.botEventBus.broadcastAuto(
            BotOfflineEvent(
                botInfo = bot.botInfo,
                throwable = null
            )
        )
        webHttpServer.close()
    }

    /**
     * 添加WebSocket转发, 让该程序作为WebSocket服务器，可以允许客户端进行连接
     */
    private fun HttpServer.addWebSocketForwarding(): HttpServer {
        if (!webHookConfig.enableWebSocketForwarding) return this
        return this.webSocketHandler { ws ->
            if (ws.path() != webHookConfig.webSocketPath){
                ws.reject()
                return@webSocketHandler
            }
            val id = UUID.randomUUID()
            logger.info("[WebSocketServer] 新连接: $id")
            var hid: Long = 1
            wsList.add(ws)

            opStart(ws)
            ws.textMessageHandler {
                val payload = it.toJsonObject().mapTo(Payload::class.java)
                when (payload.opcode) {
                    2 -> opcode2(ws, hid, id)
                    1 -> opcode1(ws, hid)
                    6 -> opcode6(ws, id)
                    else -> {
                        logger.warn("[WebSocketServer] 不支持的opcode: ${payload.opcode}")
                    }
                }
                hid++
            }

            ws.closeHandler {
                logger.info("[WebSocketServer] 断开连接: $id")
                wsList.remove(ws)
            }
        }
    }

    private fun opStart(ws: ServerWebSocket) {
        val start = Payload(
            opcode = 10,
            eventContent = jsonObjectOf("heartbeat_interval" to 45000).toJAny()
        )
        ws.writeTextMessage(start.toJsonString())
    }

    private fun opcode6(ws: ServerWebSocket, id: UUID) {
        val o6 = Payload(
            opcode = 0,
            eventType = "RESUMED",
            eventContent = "".toJAny()
        )

        ws.writeTextMessage(o6.toJsonString())
    }

    private fun opcode1(ws: ServerWebSocket, hid: Long) {
        val o1 = Payload(
            opcode = 11,
            hid = hid,
        )
        ws.writeTextMessage(o1.toJsonString())
    }

    private fun opcode2(ws: ServerWebSocket, hid: Long, id: UUID) {
        val o2 = Payload(
            opcode = 0,
            hid = hid,
            eventType = "READY",
            eventContent = """
                 {
                    "version": 1,
                    "session_id": "$id",
                    "user": {
                      "id": "",
                      "username": "",
                      "bot": true
                    },
                    "shard": [0, 0]
                  }
            """.trimIndent().toJsonObject().toJAny()
        )
        ws.writeTextMessage(o2.toJsonString())
    }
}
