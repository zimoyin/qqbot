package com.github.zimoyin.qqbot.event.handler

import com.github.zimoyin.qqbot.net.websocket.bean.Payload
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.bot.BotResumedEvent
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler


/**
 *
 * @author : zimo
 * @date : 2023/12/11
 *
 * BotResumedHandler的消息处理器
 */
class BotResumedHandler : AbsEventHandler<BotResumedEvent>() {
    override fun handle(payload: Payload): BotResumedEvent {
        val info = BotInfo.create(payload.appID!!)

        return BotResumedEvent(
            metadata = payload.metadata,
            metadataType = payload.eventType!!,
            botInfo = info
        )
    }
}