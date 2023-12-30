package com.github.zimoyin.qqbot.event.events.revoke

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.contact.Contact
import com.github.zimoyin.qqbot.bot.contact.User
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.event.events.Event
import com.github.zimoyin.qqbot.event.handler.message.MessageHandler

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 * 信息撤回事件
 */
@EventAnnotation.EventMetaType("Not_MetaType_MessageRevokeEvent")
@EventAnnotation.EventHandler(MessageHandler::class, true)
interface MessageRevokeEvent : Event {
    override val metadataType: String
    override val metadata: String
    override val botInfo: BotInfo
    val msgID: String
    val windows: Contact
    //TODO 添加Get方法从本地的聊天库里面查找消息。该聊天库为键值对形式，key为msgID，value为MessageChain对象。每个群/会话/频道对应一个聊天库
    //TODO 不具体实现聊天库，而是提供一个接口，最后通过该接口获取实现。实现由用户开发 使用 SPL 加载
    val messageChain: MessageChain
        get() = TODO("Not yet implemented")

    /**
     * 信息发送者
     */
    val sender: User

    /**
     * 操作者ID
     */
    val operatorID: String
}