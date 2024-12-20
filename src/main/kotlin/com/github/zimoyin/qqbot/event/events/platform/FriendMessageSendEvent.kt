package com.github.zimoyin.qqbot.event.events.platform

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.contact.Friend
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.event.handler.NoneEventHandler
import com.github.zimoyin.qqbot.net.bean.SendMessageResultBean

/**
 *
 * @author : zimo
 * @date : 2023/12/19
 *
 * 信息向服务器推送前触发该事件
 * 该事件为全局事件，只从全局事件总线中传递
 * 注意该事件只是作为一个通知，尽量不要使用该事件拦截与修改待发送的信息
 */
@EventAnnotation.EventMetaType("Platform_FriendMessageSendEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
data class FriendMessageSendEvent(
    override val metadata: String = "Platform_FriendMessageSendEvent",
    override val metadataType: String = "Platform_FriendMessageSendEvent",
    override val contact: Friend,
    override val botInfo: BotInfo = contact.botInfo,
    override val msgID: String,
    override val messageChain: MessageChain,
    override val result: SendMessageResultBean,
    override val eventID: String ="",
) : MessageSendEvent(
    metadata = metadata,
    metadataType = metadataType,
    msgID = msgID,
    messageChain = messageChain,
    contact = contact,
    botInfo = botInfo,
    result = result
)
