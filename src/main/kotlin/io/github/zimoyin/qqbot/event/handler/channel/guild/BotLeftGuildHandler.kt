package io.github.zimoyin.qqbot.event.handler.channel.guild


import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.events.channel.guild.BotLeftGuildEvent
import io.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import io.github.zimoyin.qqbot.net.bean.GuildBean
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.utils.JSON

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
            guild = JSON.toObject<GuildBean>(payload.eventContent.toString()),
            eventID = payload.eventID?:""
        )
    }
}
