package io.github.zimoyin.qqbot.event.handler.message

import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.events.MessageAuditPassEvent
import io.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import io.github.zimoyin.qqbot.net.bean.MessageAuditBean
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.utils.JSON

/**
 *
 * @author : zimo
 * @date : 2023/12/11
 *
 * 通用的消息处理器
 */
class MessageAuditPassHandler : AbsEventHandler<MessageAuditPassEvent>() {
    override fun handle(payload: Payload): MessageAuditPassEvent {
        val message = JSON.toObject<MessageAuditBean>(payload.eventContent.toString())
        val info = BotInfo.create(payload.appID!!)

        return MessageAuditPassEvent(
            metadata = payload.metadata,
            metadataType = payload.eventType!!,
            botInfo = info,
            message = message,
            eventID = payload.eventID?:""
        )
    }
}
