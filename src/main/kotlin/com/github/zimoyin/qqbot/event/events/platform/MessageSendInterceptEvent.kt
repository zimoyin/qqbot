package com.github.zimoyin.qqbot.event.events.platform

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.contact.Contact
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.event.handler.NoneEventHandler

/**
 *
 * @author : zimo
 * @date : 2023/12/19
 *
 * 信息被拦截触发事件
 */
@EventAnnotation.EventMetaType("Platform_MessageSendInterceptEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
class MessageSendInterceptEvent(
    override val metadata: String = "Platform_MessageSendInterceptEvent",
    override val metadataType: String = "Platform_MessageSendInterceptEvent",
    val msgID: String,
    val messageChain: MessageChain,
    val contact: Contact,
    override val botInfo: BotInfo = contact.botInfo,
) : PlatformEvent