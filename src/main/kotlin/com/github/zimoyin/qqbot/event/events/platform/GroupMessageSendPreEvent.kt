package com.github.zimoyin.qqbot.event.events.platform

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.contact.Group
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.event.handler.NoneEventHandler

/**
 *
 * @author : zimo
 * @date : 2023/12/19
 *
 * 信息向服务器推送前触发该事件
 * 该事件为全局事件，只从全局事件总线中传递
 * 注意该事件只是作为一个通知，不要使用该事件拦截与修改待发送的信息,该事件无法做到
 *
 * 如果相对发送的信息进行审核使用 MessageSendPreEvent.interceptor 方法进行拦截，方法返回 true 则继续发送，否则不发送
 */
@EventAnnotation.EventMetaType("Platform_GroupMessageSendPreEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
data class GroupMessageSendPreEvent(
    override val metadata: String = "Platform_GroupMessageSendPreEvent",
    override val metadataType: String = "Platform_GroupMessageSendPreEvent",
    override val contact: Group,
    override val botInfo: BotInfo = contact.botInfo,
    override val msgID: String,
    override var messageChain: MessageChain,
    override val eventID: String ="",
) : MessageSendPreEvent(
    metadata = metadata,
    metadataType = metadataType,
    msgID = msgID,
    messageChain = messageChain,
    contact = contact,
    botInfo = botInfo,
)
