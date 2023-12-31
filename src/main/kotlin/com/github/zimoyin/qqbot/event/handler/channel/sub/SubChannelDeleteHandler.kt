package com.github.zimoyin.qqbot.event.handler.channel.sub

import com.github.zimoyin.qqbot.net.bean.ChannelBean
import com.github.zimoyin.qqbot.net.bean.Payload
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.channel.sub.SubChannelDeleteEvent
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler


import com.github.zimoyin.qqbot.utils.JSON

/**
 *
 * @author : zimo
 * @date : 2023/12/08
 */
class SubChannelDeleteHandler : AbsEventHandler<SubChannelDeleteEvent>() {
    override fun handle(payload: Payload): SubChannelDeleteEvent {
        return SubChannelDeleteEvent(
            metadata = payload.metadata,
            botInfo = BotInfo.create(payload.appID!!),
            channelBean = JSON.toObject<ChannelBean>(payload.eventContent.toString())
        )
    }
}
