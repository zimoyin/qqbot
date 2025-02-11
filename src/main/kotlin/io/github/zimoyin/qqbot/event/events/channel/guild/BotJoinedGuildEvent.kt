package io.github.zimoyin.qqbot.event.events.channel.guild

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.channel.guild.BotJoinedGuildHandler
import io.github.zimoyin.qqbot.net.bean.GuildBean


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 机器人被加入到某个频道的时候。
 */
@EventAnnotation.EventMetaType("GUILD_CREATE")
@EventAnnotation.EventHandler(BotJoinedGuildHandler::class)
data class BotJoinedGuildEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "GUILD_CREATE",
    override  val guild: GuildBean,
    override val eventID: String ="",
) : GuildOperate
