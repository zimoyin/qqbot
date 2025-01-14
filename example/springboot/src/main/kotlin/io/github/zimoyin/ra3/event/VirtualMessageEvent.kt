package io.github.zimoyin.ra3.event

import io.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.Bot
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Contact
import io.github.zimoyin.qqbot.bot.contact.Sender
import io.github.zimoyin.qqbot.bot.contact.User
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import io.github.zimoyin.qqbot.event.handler.message.MessageHandler
import io.github.zimoyin.qqbot.event.supporter.EventMapping
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import io.github.zimoyin.qqbot.net.Token
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean
import io.vertx.core.Future
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.util.LambdaSafe.callback
import java.io.Serializable
import java.util.*

/**
 *
 * @author : zimo
 * @date : 2025/01/10
 */
@EventAnnotation.EventMetaType("VirtualMessageEvent")
@EventAnnotation.EventHandler(MessageHandler::class, true)
data class VirtualMessageEvent(
    override val metadata: String = "",
    override val botInfo: BotInfo = BotInfo(
        id = "1234567890",
        nick = "测试机器人",
        avatar = "https://q1.qlogo.cn/g?b=qq&nk=1234567890&s=640",
        unionOpenid = null,
        token = Token.createByAppSecret("102077167", "321"),
        unionUserAccount = null,
    ),
    override val messageChain: MessageChain,
    override val metadataType: String = "VirtualMessageEvent",
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
    override val windows: VirtualContact
) : MessageEvent {

    companion object {
        init {
            EventMapping.add(VirtualMessageEvent::class.java)
        }

        fun create(messageChain: MessageChain) {
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
        token = Token.createByAppSecret("102077167", "321"),
        unionUserAccount = null,
    )
) : Serializable, Contact {
    val logger: Logger = LoggerFactory.getLogger(javaClass)
    override fun send(message: MessageChain): Future<SendMessageResultBean> {
        logger.debug("发送消息：{}", message)
        return Future.succeededFuture(
            SendMessageResultBean(
                contact = this
            )
        )
    }
}