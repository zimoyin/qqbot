package io.github.zimoyin.qqbot.event.handler.friend

import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.PrivateFriend
import io.github.zimoyin.qqbot.event.events.friend.AddFriendEvent
import io.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.net.bean.message.Message
import io.github.zimoyin.qqbot.utils.JSON
import java.util.*

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
class AddFriendHandler : AbsEventHandler<AddFriendEvent>() {
    override fun handle(payload: Payload): AddFriendEvent {
        val json = JSON.toJsonObject(payload.eventContent.toString())
        val bot = BotInfo.create(payload.appID!!)
        val id = json.getString("openid")
        val user = PrivateFriend.convert(bot,id)
        return AddFriendEvent(
            metadata = payload.metadata,
            botInfo = BotInfo.create(payload.appID!!),
            timestamp = Date(json.getLong("timestamp") * 1000),
            friendID = json.getString("openid"),
            eventID = payload.eventID ?: "",
            windows = user
        )
    }
}
