package io.github.zimoyin.qqbot.event.handler.channel.sub


import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.events.channel.sub.SubChannelUpdateEvent
import io.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import io.github.zimoyin.qqbot.net.bean.ChannelBean
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.utils.JSON

/**
 *
 * @author : zimo
 * @date : 2023/12/08
 */
class SubChannelUpdateHandler : AbsEventHandler<SubChannelUpdateEvent>() {
    override fun handle(payload: Payload): SubChannelUpdateEvent {
        val json = JSON.toJsonObject(payload.eventContent.toString())
        val info = BotInfo.create(payload.appID!!)
//        json.put("botInfo", info) //未知代码在此注释
        return SubChannelUpdateEvent(
            metadata = payload.metadata,
            botInfo = info,
            channelBean = JSON.toObject<ChannelBean>(json),
            eventID = payload.eventID?:""
        )
    }
}
