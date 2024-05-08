package com.github.zimoyin.qqbot.event.handler.friend

import com.github.zimoyin.qqbot.net.bean.Payload
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.friend.OpenFriendBotEvent
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler

import com.github.zimoyin.qqbot.utils.JSON
import java.time.Instant

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
class OpenFriendBotHandler : AbsEventHandler<OpenFriendBotEvent>() {
    override fun handle(payload: Payload): OpenFriendBotEvent {
        val json = JSON.toJsonObject(payload.eventContent.toString())
        return OpenFriendBotEvent(
            metadata = payload.metadata,
            botInfo = BotInfo.create(payload.appID!!),
            timestamp = Instant.ofEpochSecond(json.getLong("timestamp")),
            friendID = json.getString("openid"),
        )
    }
}
