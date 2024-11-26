package com.github.zimoyin.qqbot.event.handler.channel.member


import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.channel.member.GuildMemberUpdateEvent
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import com.github.zimoyin.qqbot.net.bean.MemberWithGuildID
import com.github.zimoyin.qqbot.net.bean.Payload
import com.github.zimoyin.qqbot.utils.JSON

/**
 *
 * @author : zimo
 * @date : 2023/12/08
 */
class GuildMemberUpdateHandler : AbsEventHandler<GuildMemberUpdateEvent>() {
    override fun handle(payload: Payload): GuildMemberUpdateEvent {
        return GuildMemberUpdateEvent(
            metadata = payload.metadata,
            botInfo = BotInfo.create(payload.appID!!),
            member = JSON.toObject<MemberWithGuildID>(payload.eventContent.toString()),
            eventID = payload.eventID?:""
        )
    }
}
