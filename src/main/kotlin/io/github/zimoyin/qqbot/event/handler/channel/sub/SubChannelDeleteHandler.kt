package io.github.zimoyin.qqbot.event.handler.channel.sub


import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.events.channel.sub.SubChannelDeleteEvent
import io.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import io.github.zimoyin.qqbot.net.bean.ChannelBean
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.utils.JSON

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
            channelBean = JSON.toObject<ChannelBean>(payload.eventContent.toString()),
            eventID = payload.eventID?:""
        )
    }
}
