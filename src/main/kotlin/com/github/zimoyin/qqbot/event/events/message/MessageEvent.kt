package com.github.zimoyin.qqbot.event.events.message

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.contact.Contact
import com.github.zimoyin.qqbot.bot.contact.User
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import com.github.zimoyin.qqbot.bot.message.type.ReferenceMessage
import com.github.zimoyin.qqbot.event.events.Event
import com.github.zimoyin.qqbot.event.handler.message.MessageHandler
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


    /**
     * 被动回复信息
     * 注意无法通过事件发送主动信息，请查询 Content.send 方法
     */
    fun reply(message: String): Future<MessageChain> {
        return windows.send(MessageChainBuilder(msgID).append(message).build())
    }

    /**
     * 被动引用回复信息
     * 注意无法通过事件发送主动信息，请查询 Content.send 方法
     */
    fun quote(message: String): Future<MessageChain> {
        return windows.send(MessageChainBuilder(msgID).append(ReferenceMessage(msgID)).append(message).build())
    }

    /**
     * 被动回复信息
     * 注意无法通过事件发送主动信息，请查询 Content.send 方法
     * 注: 对于非文本等形式的消息，可能会受限于主动信息推送
     */
    fun reply(message: MessageChain): Future<MessageChain> {
        return windows.send(
            MessageChainBuilder(message.id ?: msgID).appendEventId(message.replyEventID).append(message).build()
        )
    }

    /**
     * 被动引用回复信息
     * 注意无法通过事件发送主动信息，请查询 Content.send 方法
     */
    fun quote(message: MessageChain): Future<MessageChain> {
        return if (message.stream().filter { it is ReferenceMessage }.count() == 0.toLong()) {
            windows.send(
                MessageChainBuilder(message.id ?: msgID).reference(msgID).appendEventId(message.replyEventID)
                    .append(message).build()
            )
        } else {
            windows.send(
                MessageChainBuilder(message.id ?: msgID).appendEventId(message.replyEventID).append(message).build()
            )
        }
    }
}
