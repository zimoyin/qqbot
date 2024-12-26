package io.github.zimoyin.qqbot.test.demo


import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.Bot
import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import io.github.zimoyin.qqbot.net.http.api.API.isDebug
import io.github.zimoyin.qqbot.net.http.TencentOpenApiHttpClient
import io.github.zimoyin.qqbot.net.webhook.WebHookConfig
import openDebug
import org.slf4j.LoggerFactory
import token

@OptIn(UntestedApi::class)
suspend fun main() {
    openDebug()
    val logger = LoggerFactory.getLogger("Main")

//    token.version = 1
    token.version = 2

    //全局事件监听
    GlobalEventBus.onEvent<Event> {
        logger.debug("全局事件监听: ${it.metadataType}")
    }

    TencentOpenApiHttpClient.isSandBox = true
    Bot.createBot(token).apply {
        println(this)
        isDebug = true
        context.set("internal.isAbnormalCardiacArrest", true)
        context.set("internal.headerCycle", 5 * 1000)
        context.set("PAYLOAD_CMD_HANDLER_DEBUG_LOG", true)
        context.set("PAYLOAD_CMD_HANDLER_DEBUG_MATA_DATA_LOG", true)
        context.set("PAYLOAD_CMD_HANDLER_DEBUG_HEART_BEAT", false)

        start(WebHookConfig("zimoyin.xyz", enableWebSocketForwarding = true)).onSuccess {
            logger.info("WebServer 启动成功 ${it.webHookConfig.port}")
        }.onFailure {
            logger.error("WebServer 启动失败", it)
        }
    }
}
