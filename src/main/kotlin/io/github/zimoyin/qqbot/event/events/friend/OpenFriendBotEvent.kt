package io.github.zimoyin.qqbot.event.events.friend

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.PrivateFriend
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import io.github.zimoyin.qqbot.bot.message.type.MessageItem
import io.github.zimoyin.qqbot.event.handler.friend.OpenFriendBotHandler
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean
import io.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future
import java.util.*

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 群管理员主动在机器人资料页操作关闭通知
 */
@EventAnnotation.EventMetaType("C2C_MSG_RECEIVE")
@EventAnnotation.EventHandler(OpenFriendBotHandler::class)
data class OpenFriendBotEvent(
    override val metadata: String,
    override val metadataType: String = "C2C_MSG_RECEIVE",
    override val botInfo: BotInfo,
    override val timestamp: Date,
    override val friendID: String,
    override val eventID: String = "",
    val windows: PrivateFriend
) : FriendBotOperationEvent {
    fun reply(msg: String): Future<SendMessageResultBean> {
        return reply(MessageChainBuilder().append(msg).build())
    }

    fun reply(message: MessageChain): Future<SendMessageResultBean> {
        val eventID = if (message.replyEventID.isNullOrEmpty()) {
            eventID
        } else {
            message.replyEventID
        }
        if (eventID.isEmpty()) return promise<SendMessageResultBean>().apply {
            tryFail("eventID is null")
        }.future()
        return windows.send(
            MessageChainBuilder().appendEventId(eventID).append(message).build()
        )
    }

    fun reply(vararg items: MessageItem): Future<SendMessageResultBean> {
        return reply(MessageChainBuilder().appendEventId(eventID).appendItems(*items).build())
    }
}
