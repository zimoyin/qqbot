package com.github.zimoyin.qqbot.event.events.channel.member

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.handler.channel.member.GuildMemberAddHandler
import com.github.zimoyin.qqbot.net.bean.MemberWithGuildID


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 新增频道成员
 */
@EventAnnotation.EventMetaType("GUILD_MEMBER_ADD")
@EventAnnotation.EventHandler(GuildMemberAddHandler::class)
data class GuildMemberAddEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "GUILD_MEMBER_ADD",
    override val member: MemberWithGuildID,
    override val eventID: String ="",
) : GuildMemberEvent
