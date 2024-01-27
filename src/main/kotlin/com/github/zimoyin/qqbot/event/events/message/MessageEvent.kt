package com.github.zimoyin.qqbot.event.events.message

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.contact.Contact
import com.github.zimoyin.qqbot.bot.contact.User
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import com.github.zimoyin.qqbot.bot.message.type.MarkdownMessage
import com.github.zimoyin.qqbot.bot.message.type.ReferenceMessage
import com.github.zimoyin.qqbot.event.events.Event
import com.github.zimoyin.qqbot.event.handler.message.MessageHandler
import com.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future
import org.slf4j.LoggerFactory

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
    fun reply(message: String) {
        windows.send(MessageChainBuilder(msgID).append(message).build())
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
     */
    fun reply(message: MessageChain): Future<MessageChain> {
        if (message.id == null && message.count { it is MarkdownMessage } <= 0) {
            val promise = promise<MessageChain>()
            promise.fail(IllegalArgumentException("MessageChain id is null. Unable to reply to message"))
            LoggerFactory.getLogger(this.javaClass)
                .error("MessageChain id is null. Please use Message ChainBuilder to build and set the ID")
            return promise.future()
        }
        return windows.send(message)
    }

    /**
     * 被动引用回复信息
     * 注意无法通过事件发送主动信息，请查询 Content.send 方法
     */
    fun quote(message: MessageChain): Future<MessageChain> {
        if (message.id == null) {
            val promise = promise<MessageChain>()
            promise.fail(IllegalArgumentException("MessageChain id is null. Unable to reply to message"))
            return promise.future()
        }
        if (message.stream().filter { it is ReferenceMessage }.count() == 0.toLong()) {
            val promise = promise<MessageChain>()
            promise.fail(IllegalArgumentException("MessageChain ReferenceMessage is null. Unable to reply to message"))
            return promise.future()
        }
        return windows.send(message)
    }
}
