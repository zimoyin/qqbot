package com.github.zimoyin.qqbot.event.handler.channel.guild


import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.channel.guild.BotLeftGuildEvent
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import com.github.zimoyin.qqbot.net.bean.GuildBean
import com.github.zimoyin.qqbot.net.bean.Payload
import com.github.zimoyin.qqbot.utils.JSON

/**
 *
 * @author : zimo
 * @date : 2023/12/08
 */
class BotLeftGuildHandler : AbsEventHandler<BotLeftGuildEvent>() {
    override fun handle(payload: Payload): BotLeftGuildEvent {
        return BotLeftGuildEvent(
            metadata = payload.metadata,
            botInfo = BotInfo.create(payload.appID!!),
            guild = JSON.toObject<GuildBean>(payload.eventContent.toString())
        )
    }
}
