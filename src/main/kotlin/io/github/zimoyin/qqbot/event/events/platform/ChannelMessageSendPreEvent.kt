package io.github.zimoyin.qqbot.event.events.platform

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler

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
@EventAnnotation.EventMetaType("Platform_ChannelMessageSendPreEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
data class ChannelMessageSendPreEvent(
    override val metadata: String = "Platform_ChannelMessageSendPreEvent",
    override val metadataType: String = "Platform_ChannelMessageSendPreEvent",
    override val contact: Channel,
    override val botInfo: BotInfo = contact.botInfo,
    override val msgID: String,
    override val eventID: String ="",
    override var messageChain: MessageChain,
) : MessageSendPreEvent(
    metadata = metadata,
    metadataType = metadataType,
    msgID = msgID,
    messageChain = messageChain,
    contact = contact,
    botInfo = botInfo,
)
