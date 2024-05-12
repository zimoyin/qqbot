package com.github.zimoyin.qqbot.event.events.revoke

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.contact.Contact
import com.github.zimoyin.qqbot.bot.contact.User
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
    /**
     * 信息发送者
     */
    val sender: User

    /**
     * 操作者ID
     */
    val operatorID: String
}
