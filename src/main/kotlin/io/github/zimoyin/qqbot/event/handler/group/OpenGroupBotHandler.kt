package io.github.zimoyin.qqbot.event.handler.group

import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.GroupImpl
import io.github.zimoyin.qqbot.event.events.group.operation.OpenGroupBotEvent
import io.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.utils.JSON
import java.util.*

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
class OpenGroupBotHandler : AbsEventHandler<OpenGroupBotEvent>() {
    override fun handle(payload: Payload): OpenGroupBotEvent {
        val json = JSON.toJsonObject(payload.eventContent.toString())
        val botInfo = BotInfo.create(payload.appID!!)
        val groupID = json.getString("group_openid")
        val eventID = payload.eventID
        return OpenGroupBotEvent(
            metadata = payload.metadata,
            botInfo = botInfo,
            timestamp = Date(json.getLong("timestamp") * 1000),
            groupID = groupID,
            opMemberOpenid = json.getString("op_member_openid"),
            windows = GroupImpl.convert(botInfo, groupID),
            eventID = eventID?:"",
        )
    }
}
