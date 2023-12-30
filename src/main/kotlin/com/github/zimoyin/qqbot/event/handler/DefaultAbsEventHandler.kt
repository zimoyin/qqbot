package com.github.zimoyin.qqbot.event.handler

import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.Event
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import com.github.zimoyin.qqbot.net.websocket.bean.Payload


/**
 *
 * @author : zimo
 * @date : 2023/12/08
 */
class DefaultAbsEventHandler : AbsEventHandler<Event>() {
    override fun handle(payload: Payload): Event {
        return object : Event {
            override val metadata: String
                get() = payload.metadata
            override val metadataType: String
                get() = payload.eventType!!
            override val botInfo: BotInfo
                get() = BotInfo.create(payload.appID!!)
        }
    }
}