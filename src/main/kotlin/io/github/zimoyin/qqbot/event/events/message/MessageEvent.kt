package io.github.zimoyin.qqbot.event.events.message

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.contact.Contact
import io.github.zimoyin.qqbot.bot.contact.User
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import io.github.zimoyin.qqbot.bot.message.type.MessageItem
import io.github.zimoyin.qqbot.bot.message.type.ReferenceMessage
import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.event.handler.message.MessageHandler
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean
import io.vertx.core.Future

/**
 *
 * @author : zimo
 * @date : 2023/12/06/18:25
 */
@EventAnnotation.EventMetaType("Not_MetaType_MessageEvent")
@EventAnnotation.EventHandler(MessageHandler::class, true)
interface MessageEvent : Event {
    /**
     * 信息的ID
     */
    val msgID: String

    /**
     * 会话窗口：通常用于信息来源。来自于哪个组，比如群组，私信等
     */
    val windows: Contact

    /**
     * 信息链
     */
    val messageChain: MessageChain

    /**
     * 发信人
     */
    val sender: User


    var msgSeq: Int

    /**
     * 被动回复信息
     * 注意无法通过事件发送主动信息，请查询 Content.send 方法
     */
    fun reply(message: String): Future<SendMessageResultBean> {
        return windows.send(MessageChainBuilder(msgID).appendMeqSeq(msgSeq++).append(message).build())
    }

    /**
     * 被动回复信息
     * 注意无法通过事件发送主动信息，请查询 Content.send 方法
     * 注: 对于非文本等形式的消息，可能会受限于主动信息推送
     */
    fun reply(vararg message: MessageItem): Future<SendMessageResultBean> {
        return reply(
            MessageChainBuilder(msgID).appendMeqSeq(msgSeq++).appendItems(*message).build()
        )
    }

    /**
     * 被动回复信息
     * 注意无法通过事件发送主动信息，请查询 Content.send 方法
     * 注: 对于非文本等形式的消息，可能会受限于主动信息推送
     */
    fun reply(message: MessageChain): Future<SendMessageResultBean> {
        return windows.send(
            MessageChainBuilder(message.id ?: msgID).appendMeqSeq(msgSeq++).appendEventId(message.replyEventID)
                .append(message).build()
        )
    }


    /**
     * 被动引用回复信息
     * 注意无法通过事件发送主动信息，请查询 Content.send 方法
     */
    fun quote(message: String): Future<SendMessageResultBean> {
        return windows.send(
            MessageChainBuilder(msgID).appendMeqSeq(msgSeq++).append(ReferenceMessage(msgID)).append(message).build()
        )
    }

    /**
     * 被动引用回复信息
     * 注意无法通过事件发送主动信息，请查询 Content.send 方法
     */
    fun quote(message: MessageChain): Future<SendMessageResultBean> {
        return if (message.stream().filter { it is ReferenceMessage }.count() == 0.toLong()) {
            windows.send(
                MessageChainBuilder(message.id ?: msgID)
                    .appendMeqSeq(msgSeq++)
                    .reference(msgID)
                    .appendEventId(message.replyEventID)
                    .append(message)
                    .build()
            )
        } else {
            windows.send(
                MessageChainBuilder(message.id ?: msgID)
                    .appendMeqSeq(msgSeq++)
                    .appendEventId(message.replyEventID)
                    .append(message)
                    .build()
            )
        }
    }


    fun qute(vararg message: MessageItem): Future<SendMessageResultBean> {
        return reply(MessageChainBuilder(msgID).appendMeqSeq(msgSeq++).appendItems(*message).build())
    }
}
