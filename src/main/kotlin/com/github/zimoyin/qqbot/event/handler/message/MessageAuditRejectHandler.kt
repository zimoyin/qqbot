package com.github.zimoyin.qqbot.event.handler.message

import com.github.zimoyin.qqbot.net.websocket.bean.MessageAuditBean
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.MessageAuditRejectEvent
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler

import com.github.zimoyin.qqbot.utils.JSON
import com.github.zimoyin.qqbot.net.websocket.bean.Payload
/**
 *
 * @author : zimo
 * @date : 2023/12/11
 *
 * 通用的消息处理器
 */
class MessageAuditRejectHandler : AbsEventHandler<MessageAuditRejectEvent>() {
    override fun handle(payload: Payload): MessageAuditRejectEvent {
        val message = JSON.toObject<MessageAuditBean>(payload.eventContent.toString())
        val info = BotInfo.create(payload.appID!!)

        return MessageAuditRejectEvent(
            metadata = payload.metadata,
            metadataType = payload.eventType!!,
            botInfo = info,
            message = message
        )
    }
}