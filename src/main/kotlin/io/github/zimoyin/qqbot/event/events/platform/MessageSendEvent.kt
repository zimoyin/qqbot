package io.github.zimoyin.qqbot.event.events.platform

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Contact
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean

/**
 *
 * @author : zimo
 * @date : 2023/12/19
 *
 * 信息向服务器推送前触发该事件
 * 该事件为全局事件，只从全局事件总线中传递
 * 注意该事件只是作为一个通知，尽量不要使用该事件拦截与修改待发送的信息
 */
@EventAnnotation.EventMetaType("Platform_MessageSendEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
open class MessageSendEvent(
    override val metadata: String = "Platform_MessageSendEvent",
    override val metadataType: String = "Platform_MessageSendEvent",
    open val msgID: String,
    open val messageChain: MessageChain,
    open val result: SendMessageResultBean, //服务器返回的信息
    open val contact: Contact,
    override val botInfo: BotInfo = contact.botInfo,
    override val eventID: String ="",
) : PlatformEvent
