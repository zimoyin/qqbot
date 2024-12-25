package io.github.zimoyin.qqbot.net.webhook

import io.github.zimoyin.qqbot.LocalLogger
import io.github.zimoyin.qqbot.bot.Bot
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.events.platform.bot.BotOfflineEvent
import io.github.zimoyin.qqbot.event.events.platform.bot.BotOnlineEvent
import io.github.zimoyin.qqbot.net.webhook.handler.PayloadCmdHandler
import io.github.zimoyin.qqbot.net.webhook.handler.Verify
import io.github.zimoyin.qqbot.utils.ex.await
import io.github.zimoyin.qqbot.utils.ex.isInitialStage
import io.vertx.core.AbstractVerticle
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.net.KeyStoreOptions
import io.vertx.core.net.PemKeyCertOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.slf4j.LoggerFactory

/**
 *
 * @author : zimo
 * @date : 2024/12/21
 */
class WebHookHttpServer(
    private val promise: Promise<HttpServer>,
    val bot: Bot,
    val webHookConfig: WebHookConfig,
) : CoroutineVerticle() {
    lateinit var router: Router
        private set

    private lateinit var webHttpServer: HttpServer
    private val logger = LocalLogger(WebHookHttpServer::class.java)
    private val payloadCmdHandler: PayloadCmdHandler = PayloadCmdHandler(bot)

    fun init() {
        router = Router.router(vertx)
        router.route("/").handler {
            val request = it.request()
            val response = it.response()

            response.setChunked(true)
            request.bodyHandler {
                kotlin.runCatching {
                    payloadCmdHandler.handle(request.headers(), it, response)
                    response.end()
                }.onFailure {
                    response.end()
                    throw it
                }
            }
        }
    }

    override suspend fun start() {
        kotlin.runCatching {
            init()
            vertx.createHttpServer(webHookConfig.options).apply {
                webHttpServer = this
            }.requestHandler(router)
                .listen(webHookConfig.port, webHookConfig.host).onSuccess {
                    if (promise.isInitialStage()) logger.info("WebHookHttpServer启动成功: ${webHookConfig.host}:${webHookConfig.port}")
                    promise.tryComplete(webHttpServer)
                }.onFailure {
                    if (promise.isInitialStage()) logger.error("WebHookHttpServer启动失败", it)
                    promise.tryFail(it)
                }
        }.onFailure {
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

}
