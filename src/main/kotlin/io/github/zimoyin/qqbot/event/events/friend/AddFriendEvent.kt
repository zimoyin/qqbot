package io.github.zimoyin.qqbot.event.events.friend

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.PrivateFriend
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import io.github.zimoyin.qqbot.bot.message.type.MessageItem
import io.github.zimoyin.qqbot.event.handler.friend.AddFriendHandler
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean
import io.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future
import java.util.*

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 用户添加机器人'好友'到消息列表
 */

@EventAnnotation.EventMetaType("FRIEND_ADD")
@EventAnnotation.EventHandler(AddFriendHandler::class)
data class AddFriendEvent(
    override val metadata: String,
    override val metadataType: String = "FRIEND_ADD",
    override val botInfo: BotInfo,
    override val timestamp: Date,
    override val friendID: String,
    override val eventID: String = "",
    val windows: PrivateFriend,
) : FriendUpdateEvent {
    var msgSeq: Int = 1
    fun reply(msg: String): Future<SendMessageResultBean> {
        msgSeq++
        return reply(MessageChainBuilder().appendMeqSeq(msgSeq).append(msg).build())
    }

    fun reply(message: MessageChain): Future<SendMessageResultBean> {
        msgSeq++
        val eventID = if (message.replyEventID.isNullOrEmpty()) {
            eventID
        } else {
            message.replyEventID
        }
        if (eventID.isEmpty()) return promise<SendMessageResultBean>().apply {
            tryFail("eventID is null")
        }.future()
        return windows.send(
            MessageChainBuilder().appendMeqSeq(msgSeq).appendEventId(eventID).append(message).build()
        )
    }

    fun reply(vararg items: MessageItem): Future<SendMessageResultBean> {
        msgSeq++
        return reply(MessageChainBuilder().appendMeqSeq(msgSeq).appendEventId(eventID).appendItems(*items).build())
    }
}
