package io.github.zimoyin.qqbot.event.events.channel.member

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.channel.member.GuildMemberUpdateHandler
import io.github.zimoyin.qqbot.net.bean.MemberWithGuildID


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 频道成员更新事件
 */
@EventAnnotation.EventMetaType("GUILD_MEMBER_UPDATE")
@EventAnnotation.EventHandler(GuildMemberUpdateHandler::class)
data class GuildMemberUpdateEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "GUILD_MEMBER_UPDATE",
    override val member: MemberWithGuildID,
    override val eventID: String ="",
) : GuildMemberEvent
