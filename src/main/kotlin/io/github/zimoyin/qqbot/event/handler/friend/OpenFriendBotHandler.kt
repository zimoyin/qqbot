package io.github.zimoyin.qqbot.event.handler.friend

import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.PrivateFriend
import io.github.zimoyin.qqbot.event.events.friend.OpenFriendBotEvent
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
class OpenFriendBotHandler : AbsEventHandler<OpenFriendBotEvent>() {
    override fun handle(payload: Payload): OpenFriendBotEvent {
        val json = JSON.toJsonObject(payload.eventContent.toString())
        val bot = BotInfo.create(payload.appID!!)
        val id = json.getString("openid")
        val user = PrivateFriend.convert(bot, id)
        return OpenFriendBotEvent(
            metadata = payload.metadata,
            botInfo = bot,
            timestamp = Date(json.getLong("timestamp") * 1000),
            friendID = id,
            eventID = payload.eventID ?: "",
            windows = user
        )
    }
}
