package io.github.zimoyin.qqbot.event.handler.group

import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.GroupImpl
import io.github.zimoyin.qqbot.event.events.group.member.AddGroupEvent
import io.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.utils.JSON
import java.util.*

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
class AddGroupHandler : AbsEventHandler<AddGroupEvent>() {
    override fun handle(payload: Payload): AddGroupEvent {
        val json = JSON.toJsonObject(payload.eventContent.toString())
        val eventID = payload.eventID
        val groupID = json.getString("group_openid")
        val botInfo = BotInfo.create(payload.appID!!)
        return AddGroupEvent(
            metadata = payload.metadata,
            botInfo = botInfo,
            groupID = json.getString("group_openid"),
            opMemberOpenid = json.getString("op_member_openid"),
            windows = GroupImpl.convert(botInfo, groupID),
            eventID = eventID?:"",
            timestamp = Date(json.getLong("timestamp") * 1000),
        )
    }
}
