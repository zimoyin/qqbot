package com.github.zimoyin.qqbot.event.events.channel.guild

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.handler.channel.guild.BotGuildUpdatedHandler
import com.github.zimoyin.qqbot.net.bean.GuildBean


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
) : GuildOperate
