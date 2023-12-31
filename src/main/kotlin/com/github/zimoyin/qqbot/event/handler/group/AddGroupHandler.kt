package com.github.zimoyin.qqbot.event.handler.group

import com.github.zimoyin.qqbot.net.bean.Payload
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.group.AddGroupEvent
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler

import com.github.zimoyin.qqbot.utils.JSON

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
class AddGroupHandler : AbsEventHandler<AddGroupEvent>() {
    override fun handle(payload: Payload): AddGroupEvent {
        val json = JSON.toJsonObject(payload.eventContent.toString())
        return AddGroupEvent(
            metadata = payload.metadata,
            botInfo = BotInfo.create(payload.appID!!),
            timestamp = json.getInstant("timestamp"),
            groupID = json.getString("group_openid"),
            opMemberOpenid = json.getString("op_member_openid"),
        )
    }
}
