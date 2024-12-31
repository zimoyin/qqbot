package io.github.zimoyin.qqbot.test

import io.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.message.type.CustomKeyboard
import io.github.zimoyin.qqbot.bot.message.type.KeyboardMessage
import io.github.zimoyin.qqbot.bot.message.type.customKeyboard
import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.event.events.platform.bot.BotOnlineEvent
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import io.github.zimoyin.qqbot.utils.io
import io.github.zimoyin.qqbot.utils.thread
import kotlinx.coroutines.*
import openDebug
import java.io.File
import java.net.URI

/**
 *
 * @author : zimo
 * @date : 2024/12/26
 */
suspend fun main() {
    for (i in 0 until 5) {
        println("准备... $i")
        delay(1000)
    }

    for (i in 0 until 10) {
        delay(500)
        println("启动中... $i")
    }
}
