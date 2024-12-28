package io.github.zimoyin.qqbot.test

import io.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.event.events.platform.bot.BotOnlineEvent
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import openDebug
import java.net.URI

/**
 *
 * @author : zimo
 * @date : 2024/12/26
 */
fun main() {
    GlobalEventBus.onEvent<Event> {
        println(it)
    }
    GlobalEventBus.broadcastAuto(
        BotOnlineEvent(
            metadata = "payload.metadata",
            botInfo = BotInfo.emptyBotInfo()
        )
    )
}
