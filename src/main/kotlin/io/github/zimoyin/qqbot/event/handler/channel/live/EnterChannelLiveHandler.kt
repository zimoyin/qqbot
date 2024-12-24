package io.github.zimoyin.qqbot.event.handler.channel.live


import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.events.channel.live.UserEnteredChannelLiveEvent
import io.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.net.bean.UserLive
import io.github.zimoyin.qqbot.utils.JSON

/**
 *
 * @author : zimo
 * @date : 2023/12/08
 */
class EnterChannelLiveHandler : AbsEventHandler<UserEnteredChannelLiveEvent>() {
    override fun handle(payload: Payload): UserEnteredChannelLiveEvent {
        return UserEnteredChannelLiveEvent(
            metadata = payload.metadata,
            botInfo = BotInfo.create(payload.appID!!),
            live = JSON.toObject<UserLive>(payload.eventContent.toString()),
            eventID = payload.eventID?:""
        )
    }
}
