package com.github.zimoyin.qqbot.event.events.channel.member

import com.github.zimoyin.qqbot.net.bean.MemberWithGuildID
import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import com.github.zimoyin.qqbot.event.events.channel.ChannelEvent
import com.github.zimoyin.qqbot.event.handler.NoneEventHandler


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 频道成员事件
 * TODO 该事件以及子事件未经任何测试
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
