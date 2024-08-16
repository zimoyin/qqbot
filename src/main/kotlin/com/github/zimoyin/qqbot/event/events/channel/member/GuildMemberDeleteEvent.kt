package com.github.zimoyin.qqbot.event.events.channel.member

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.handler.channel.member.GuildMemberDeleteHandler
import com.github.zimoyin.qqbot.net.bean.MemberWithGuildID


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 频道成员离开
 */
@EventAnnotation.EventMetaType("GUILD_MEMBER_REMOVE")
@EventAnnotation.EventHandler(GuildMemberDeleteHandler::class)
data class GuildMemberDeleteEvent(
    override val metadataType: String = "GUILD_MEMBER_REMOVE",
    override val metadata: String,
    override val botInfo: BotInfo,
    override val member: MemberWithGuildID,
    override val eventID: String ="",
) : GuildMemberEvent
