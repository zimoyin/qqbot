package io.github.zimoyin.qqbot.event.events.channel.guild

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.channel.guild.BotLeftGuildHandler
import io.github.zimoyin.qqbot.net.bean.GuildBean


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 机器人被离开到某个频道的时候。
 * 频道被解散
 * 机器人被移除
 */
@EventAnnotation.EventMetaType("GUILD_DELETE")
@EventAnnotation.EventHandler(BotLeftGuildHandler::class)
data class BotLeftGuildEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "GUILD_DELETE",
    override  val guild: GuildBean,
    override val eventID: String ="",
) : GuildOperate
