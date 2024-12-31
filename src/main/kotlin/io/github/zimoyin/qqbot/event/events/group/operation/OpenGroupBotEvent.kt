package io.github.zimoyin.qqbot.event.events.group.operation

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Contact
import io.github.zimoyin.qqbot.bot.contact.GroupImpl
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import io.github.zimoyin.qqbot.bot.message.type.MessageItem
import io.github.zimoyin.qqbot.event.events.operation.OpenBotOperationEvent
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean
import io.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future
import java.util.*

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 群管理员主动在机器人资料页操作开启通知
 */
@EventAnnotation.EventMetaType("GROUP_MSG_RECEIVE")
@EventAnnotation.EventHandler(io.github.zimoyin.qqbot.event.handler.group.OpenGroupBotHandler::class)
data class OpenGroupBotEvent(
    override val metadata: String,
    override val metadataType: String = "GROUP_MSG_RECEIVE",
    override val botInfo: BotInfo,
    override val groupID: String,
    override val timestamp: Date,
    override val opMemberOpenid: String,
    val windows: GroupImpl,
    override val eventID: String
) : OpenBotOperationEvent,GroupBotOperationEvent{
    var msgSeq: Int = 1
    fun reply(msg: String): Future<SendMessageResultBean> {
        return reply(MessageChainBuilder().appendMeqSeq(msgSeq++).append(msg).build())
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
            MessageChainBuilder().appendMeqSeq(msgSeq++).appendEventId(eventID).append(message).build()
        )
    }

    fun reply(vararg items: MessageItem): Future<SendMessageResultBean> {
        return reply(MessageChainBuilder().appendMeqSeq(msgSeq++).appendEventId(eventID).appendItems(*items).build())
    }
}
