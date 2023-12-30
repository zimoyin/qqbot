package com.github.zimoyin.qqbot.event.handler.channel.live

import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.channel.live.UserExitChannelLiveEvent
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler


import com.github.zimoyin.qqbot.utils.JSON
import com.github.zimoyin.qqbot.net.websocket.bean.Payload
import com.github.zimoyin.qqbot.net.websocket.bean.UserLive

/**
 *
 * @author : zimo
 * @date : 2023/12/08
 */
class ExitChannelLiveHandler : AbsEventHandler<UserExitChannelLiveEvent>() {
    override fun handle(payload: Payload): UserExitChannelLiveEvent {
        return UserExitChannelLiveEvent(
            metadata = payload.metadata,
            botInfo = BotInfo.create(payload.appID!!),
            live = JSON.toObject<UserLive>(payload.eventContent.toString())
        )
    }
}