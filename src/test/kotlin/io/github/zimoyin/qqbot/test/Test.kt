package io.github.zimoyin.qqbot.test

import io.github.zimoyin.qqbot.LocalLogger
import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Contact
import io.github.zimoyin.qqbot.bot.contact.Sender
import io.github.zimoyin.qqbot.bot.contact.User
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import io.github.zimoyin.qqbot.event.events.message.PrivateChannelMessageEvent
import io.github.zimoyin.qqbot.event.events.platform.bot.BotOnlineEvent
import io.github.zimoyin.qqbot.event.handler.message.MessageHandler
import io.github.zimoyin.qqbot.event.supporter.EventMapping
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus.bus
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus.debugLogger
import io.github.zimoyin.qqbot.net.Token
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean
import io.github.zimoyin.qqbot.utils.ex.md5
import io.github.zimoyin.qqbot.utils.io
import io.github.zimoyin.qqbot.utils.vertxWorker
import io.vertx.core.Future
import io.vertx.core.Vertx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import openDebug
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.util.*
import kotlin.random.Random

/**
 *
 * @author : zimo
 * @date : 2024/12/26
 */
fun main() {
    val a = Random(5).nextBytes(1024*1024)
    println(a.md5())
    println(a.md5())
}

