package com.github.zimoyin.qqbot.event.handler.group

import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.group.operation.CloseGroupBotEvent
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import com.github.zimoyin.qqbot.net.bean.Payload
import com.github.zimoyin.qqbot.utils.JSON
import java.util.*

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
class CloseGroupBotHandler : AbsEventHandler<CloseGroupBotEvent>() {
    override fun handle(payload: Payload): CloseGroupBotEvent {
        val json = JSON.toJsonObject(payload.eventContent.toString())
        return CloseGroupBotEvent(
            metadata = payload.metadata,
            botInfo = BotInfo.create(payload.appID!!),
            timestamp = Date(json.getLong("timestamp") * 1000),
            groupID = json.getString("group_openid"),
            opMemberOpenid = json.getString("op_member_openid"),
            eventID = payload.eventID?:""
        )
    }
}
