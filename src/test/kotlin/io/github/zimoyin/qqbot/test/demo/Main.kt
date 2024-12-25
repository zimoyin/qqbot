package io.github.zimoyin.qqbot.test.demo


import io.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.Bot
import io.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import io.github.zimoyin.qqbot.bot.message.type.ImageMessage
import io.github.zimoyin.qqbot.bot.message.type.PlainTextMessage
import io.github.zimoyin.qqbot.bot.onEvent
import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import io.github.zimoyin.qqbot.net.Intents
import io.github.zimoyin.qqbot.net.http.api.TencentOpenApiHttpClient
import io.github.zimoyin.qqbot.net.webhook.WebHookConfig
import io.vertx.core.http.WebSocketClientOptions
import kotlinx.coroutines.delay
import openDebug
import org.slf4j.LoggerFactory
import token

@OptIn(UntestedApi::class)
suspend fun main() {
    openDebug()
    val logger = LoggerFactory.getLogger("Main")

    token.version = 1
//    token.version = 2

    //全局事件监听
    GlobalEventBus.onEvent<Event> {
        logger.debug("全局事件监听: ${it.metadataType}")
    }

    TencentOpenApiHttpClient.isSandBox = true
    Bot.createBot(token).apply {
        start(WebHookConfig("./127.0.0.1", enableWebSocketForwarding = true)).onSuccess {
            logger.info("WebServer 启动成功 ${it.webHookConfig.port}")
        }.onFailure {
            logger.error("WebServer 启动失败", it)
        }
    }
}
