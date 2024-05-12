package com.github.zimoyin.qqbot.event.handler.channel.sub


import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.channel.sub.SubChannelCreateEvent
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import com.github.zimoyin.qqbot.net.bean.ChannelBean
import com.github.zimoyin.qqbot.net.bean.Payload
import com.github.zimoyin.qqbot.utils.JSON


/**
 *
 * @author : zimo
 * @date : 2023/12/08
 */
class SubChannelCreateHandler : AbsEventHandler<SubChannelCreateEvent>() {
    override fun handle(payload: Payload): SubChannelCreateEvent {
        return SubChannelCreateEvent(
            metadata = payload.metadata,
            botInfo = BotInfo.create(payload.appID!!),
            channelBean = JSON.toObject<ChannelBean>(payload.eventContent.toString())
        )
    }
}
