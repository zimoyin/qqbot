package io.github.zimoyin.qqbot.event.events.channel.guild

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.channel.guild.BotGuildUpdatedHandler
import io.github.zimoyin.qqbot.net.bean.GuildBean


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 频道信息变更
 * 事件内容为变更后的数据
 */
@EventAnnotation.EventMetaType("GUILD_UPDATE")
@EventAnnotation.EventHandler(BotGuildUpdatedHandler::class)
data class BotGuildUpdatedEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "GUILD_UPDATE",
    override val guild: GuildBean,
    override val eventID: String ="",
) : GuildOperate
