package com.github.zimoyin.qqbot.event.handler.friend

import com.github.zimoyin.qqbot.net.websocket.bean.Payload
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.friend.CloseFriendBotEvent
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler

import com.github.zimoyin.qqbot.utils.JSON

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
class CloseFriendBotHandler : AbsEventHandler<CloseFriendBotEvent>() {
    override fun handle(payload: Payload): CloseFriendBotEvent {
        val json = JSON.toJsonObject(payload.eventContent.toString())
        return CloseFriendBotEvent(
            metadata = payload.metadata,
            botInfo = BotInfo.create(payload.appID!!),
            timestamp = json.getInstant("timestamp"),
            friendID = json.getString("openid"),
        )
    }
}