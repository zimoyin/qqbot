package com.github.zimoyin.qqbot.event.handler.friend

import com.github.zimoyin.qqbot.net.websocket.bean.Payload
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.friend.DeleteFriendEvent
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler

import com.github.zimoyin.qqbot.utils.JSON

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
class DeleteFriendHandler : AbsEventHandler<DeleteFriendEvent>() {
    override fun handle(payload: Payload): DeleteFriendEvent {
        val json = JSON.toJsonObject(payload.eventContent.toString())
        return DeleteFriendEvent(
            metadata = payload.metadata,
            botInfo = BotInfo.create(payload.appID!!),
            timestamp = json.getInstant("timestamp"),
            friendID = json.getString("openid"),
        )
    }
}