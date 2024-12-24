package io.github.zimoyin.qqbot.event.handler

import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.events.bot.BotResumedEvent
import io.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import io.github.zimoyin.qqbot.net.bean.Payload


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
            botInfo = info,
            eventID = payload.eventID?:""
        )
    }
}
