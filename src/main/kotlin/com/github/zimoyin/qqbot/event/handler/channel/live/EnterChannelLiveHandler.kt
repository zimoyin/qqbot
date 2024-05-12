package com.github.zimoyin.qqbot.event.handler.channel.live


import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.channel.live.UserEnteredChannelLiveEvent
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import com.github.zimoyin.qqbot.net.bean.Payload
import com.github.zimoyin.qqbot.net.bean.UserLive
import com.github.zimoyin.qqbot.utils.JSON

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
            live = JSON.toObject<UserLive>(payload.eventContent.toString())
        )
    }
}
