package io.github.zimoyin.qqbot.event.events

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.events.platform.PlatformEvent
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler
import io.github.zimoyin.qqbot.event.handler.message.MessageAuditPassHandler
import io.github.zimoyin.qqbot.event.handler.message.MessageAuditRejectHandler
import io.github.zimoyin.qqbot.net.bean.MessageAuditBean


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
data class MessageStartAuditEvent(
    override val botInfo: BotInfo,
    override val metadata: String = "Platform_MessageAuditEvent",
    override val metadataType: String = "Platform_MessageAuditEvent",
    override val eventID: String ="",
) : PlatformEvent, MessageAuditEvent

@EventAnnotation.EventMetaType("MESSAGE_AUDIT_PASS")
@EventAnnotation.EventHandler(MessageAuditPassHandler::class)
data class MessageAuditPassEvent(
    override val botInfo: BotInfo,
    override val metadata: String,
    override val metadataType: String = "MESSAGE_AUDIT_PASS",
    val message: MessageAuditBean,
    override val eventID: String ="",
) : MessageAuditEvent


/**
 * 信息审核不通过
 */
@EventAnnotation.EventMetaType("MESSAGE_AUDIT_REJECT")
@EventAnnotation.EventHandler(MessageAuditRejectHandler::class)
data class MessageAuditRejectEvent(
    override val botInfo: BotInfo,
    override val metadata: String,
    override val metadataType: String = "MESSAGE_AUDIT_REJECT",
    val message: MessageAuditBean,
    override val eventID: String ="",
) : MessageAuditEvent
