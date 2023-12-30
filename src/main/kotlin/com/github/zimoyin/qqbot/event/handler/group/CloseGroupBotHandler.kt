package com.github.zimoyin.qqbot.event.handler.group

import com.github.zimoyin.qqbot.net.websocket.bean.Payload
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.group.CloseGroupBotEvent
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler

import com.github.zimoyin.qqbot.utils.JSON

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
            timestamp = json.getInstant("timestamp"),
            groupID = json.getString("group_openid"),
            opMemberOpenid = json.getString("op_member_openid"),
        )
    }
}