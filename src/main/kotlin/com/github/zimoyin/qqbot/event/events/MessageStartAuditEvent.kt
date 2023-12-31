package com.github.zimoyin.qqbot.event.events

import com.github.zimoyin.qqbot.net.bean.MessageAuditBean
import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.platform.PlatformEvent
import com.github.zimoyin.qqbot.event.handler.NoneEventHandler
import com.github.zimoyin.qqbot.event.handler.message.MessageAuditPassHandler
import com.github.zimoyin.qqbot.event.handler.message.MessageAuditRejectHandler


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 信息审核事件
 */
@EventAnnotation.EventMetaType("Platform_MessageAuditEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
interface MessageAuditEvent : Event {
    override val botInfo: BotInfo
    override val metadata: String
    override val metadataType: String
}

@EventAnnotation.EventMetaType("Platform_MessageStartAuditEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
class MessageStartAuditEvent(
    override val botInfo: BotInfo,
    override val metadata: String = "Platform_MessageAuditEvent",
    override val metadataType: String = "Platform_MessageAuditEvent",
) : PlatformEvent, MessageAuditEvent

@EventAnnotation.EventMetaType("MESSAGE_AUDIT_PASS")
@EventAnnotation.EventHandler(MessageAuditPassHandler::class)
class MessageAuditPassEvent(
    override val botInfo: BotInfo,
    override val metadata: String,
    override val metadataType: String = "MESSAGE_AUDIT_PASS",
    val message: MessageAuditBean,
) : MessageAuditEvent

@EventAnnotation.EventMetaType("MESSAGE_AUDIT_REJECT")
@EventAnnotation.EventHandler(MessageAuditRejectHandler::class)
class MessageAuditRejectEvent(
    override val botInfo: BotInfo,
    override val metadata: String,
    override val metadataType: String = "MESSAGE_AUDIT_REJECT",
    val message: MessageAuditBean,
) : MessageAuditEvent
