package io.github.zimoyin.qqbot.net.webhook

import io.github.zimoyin.qqbot.LocalLogger
import io.github.zimoyin.qqbot.bot.Bot
import io.github.zimoyin.qqbot.event.events.platform.bot.BotOfflineEvent
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.net.webhook.handler.PayloadCmdHandler
import io.github.zimoyin.qqbot.utils.cpu
import io.github.zimoyin.qqbot.utils.ex.*
import io.vertx.core.Future
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
    private lateinit var payloadCmdHandler: PayloadCmdHandler
    val wsList = mutableListOf<ServerWebSocket>()

    fun init() {
        payloadCmdHandler = PayloadCmdHandler(bot, vertx)
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

    fun close(): Future<Void> {
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
