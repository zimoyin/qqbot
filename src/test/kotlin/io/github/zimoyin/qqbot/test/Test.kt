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
import java.time.LocalDateTime
import java.time.LocalTime

/**
 *
 * @author : zimo
 * @date : 2024/12/26
 */
suspend fun main() {
    val time: Int = (System.currentTimeMillis() / 1000).toInt()
    val now = LocalTime.now()
    println(time)
    println(now.second)
    println(System.currentTimeMillis())
}
