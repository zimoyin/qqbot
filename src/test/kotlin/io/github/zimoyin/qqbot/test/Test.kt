package io.github.zimoyin.qqbot.test

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

/**
 *
 * @author : zimo
 * @date : 2024/12/26
 */
fun main() {
    openDebug()
    GlobalEventBus.onEvent<MessageEvent> {
        println(15262)
    }
    Thread.sleep(1000)
    VirtualMessageEvent.create(MessageChain.builder().append("测试消息").build())
    println("end")
}

@EventAnnotation.EventMetaType("Not_MetaType_MessageEvent")
@EventAnnotation.EventHandler(MessageHandler::class, true)
data class VirtualMessageEvent(
    override val metadata: String = "",
    override val botInfo: BotInfo = BotInfo(
        id = "1234567890",
        nick = "测试机器人",
        avatar = "https://q1.qlogo.cn/g?b=qq&nk=1234567890&s=640",
        unionOpenid = null,
        token = Token.createByAppSecret("123", "321"),
        unionUserAccount = null,
    ),
    override val messageChain: MessageChain,
    override val metadataType: String = "TEST:NULL",
    override val msgID: String = "TEST:NULL",
    override var msgSeq: Int = 1,
    override val sender: User = Sender(
        id = "TEST:NULL",
        nick = "TEST:NULL",
        isBot = false,
        avatar = "TEST:NULL",
        roles = emptyList(),
        joinedAt = Date(),
        unionOpenID = null,
        unionUserAccount = null,
        botInfo = botInfo
    ),
    override val windows: Contact
) : MessageEvent {
    companion object {
        fun create(messageChain: MessageChain) {
            EventMapping.add(VirtualMessageEvent::class.java)
            val event = VirtualMessageEvent(
                messageChain = messageChain,
                windows = VirtualContact()
            )
            GlobalEventBus.broadcastAuto(event)
        }
    }
}

data class VirtualContact(
    override val id: String = "1234567890",
    override val botInfo: BotInfo = BotInfo(
        id = "1234567890",
        nick = "测试机器人",
        avatar = "https://q1.qlogo.cn/g?b=qq&nk=1234567890&s=640",
        unionOpenid = null,
        token = Token.createByAppSecret("123", "321"),
        unionUserAccount = null,
    )
) : Serializable, Contact {
    val logger: Logger = LoggerFactory.getLogger(javaClass)
    override fun send(message: MessageChain): Future<SendMessageResultBean> {
        var sb = ""
        for (messageItem in message) {
            sb += messageItem.toString()
        }
        logger.debug("SEND MESSAGE: \n$sb\n")
        return Future.succeededFuture(
            SendMessageResultBean(
                contact = this
            )
        )
    }
}
