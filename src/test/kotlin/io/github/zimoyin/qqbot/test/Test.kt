package io.github.zimoyin.qqbot.test

import io.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import io.github.zimoyin.qqbot.bot.BotInfo
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
fun main() = repeat(1_000) {
    io { //12S
        println(Thread.currentThread().name)
        File("G:\\resources\\office.7z").readBytes()
    }
}
