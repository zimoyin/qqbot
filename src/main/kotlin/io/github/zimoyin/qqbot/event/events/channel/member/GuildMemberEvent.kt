package io.github.zimoyin.qqbot.event.events.channel.member

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import io.github.zimoyin.qqbot.event.events.channel.ChannelEvent
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler
import io.github.zimoyin.qqbot.net.bean.MemberWithGuildID


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 频道成员事件
 */
@EventAnnotation.EventMetaType("Not_MetaType_ChannelMemberEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
interface GuildMemberEvent : ChannelEvent {
    override val metadataType: String
        get() = "Not_MetaType_ChannelMemberEvent"

    /**
     * 成员Bean
     */
    val member: MemberWithGuildID

    override val channel: Channel
        get() = ChannelImpl.convert(botInfo,member.guildID!!,null,member.guildID)
}
