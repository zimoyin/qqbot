package com.github.zimoyin.qqbot.event.handler.group

import com.github.zimoyin.qqbot.net.bean.Payload
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.group.operation.OpenGroupBotEvent
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler

import com.github.zimoyin.qqbot.utils.JSON
import java.time.Instant

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
class OpenGroupBotHandler : AbsEventHandler<OpenGroupBotEvent>() {
    override fun handle(payload: Payload): OpenGroupBotEvent {
        val json = JSON.toJsonObject(payload.eventContent.toString())
        return OpenGroupBotEvent(
            metadata = payload.metadata,
            botInfo = BotInfo.create(payload.appID!!),
            timestamp = Instant.ofEpochSecond(json.getLong("timestamp")),
            groupID = json.getString("group_openid"),
            opMemberOpenid = json.getString("op_member_openid"),
        )
    }
}
