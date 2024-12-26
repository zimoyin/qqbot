package io.github.zimoyin.qqbot.net.webhook

import io.github.zimoyin.qqbot.LocalLogger
import io.github.zimoyin.qqbot.bot.Bot
import io.github.zimoyin.qqbot.event.events.platform.bot.BotOfflineEvent
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.net.http.TencentOpenApiHttpClient
import io.github.zimoyin.qqbot.net.webhook.handler.PayloadCmdHandler
import io.github.zimoyin.qqbot.net.webhook.handler.WebSocketServerHandler
import io.github.zimoyin.qqbot.utils.ex.*
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

        if (webHookConfig.enableWebSocketForwarding) router.route("/*").handler {
            val request = it.request()
            val response = it.response()
            response.setChunked(true)

            request.bodyHandler { body ->
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
                        response.end(res.body())
                    }.onFailure {
                        response.statusCode = 502
                        response.end(jsonObjectOf("code" to 502, "message" to "转发服务器出现异常").toString())
                        logger.warn(
                            "转发服务器出现异常: [${request.method()}] ${request.path()} \n headers: ${request.headers()} \n body: ${body.writeToText()}",
                            it
                        )
                    }
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
