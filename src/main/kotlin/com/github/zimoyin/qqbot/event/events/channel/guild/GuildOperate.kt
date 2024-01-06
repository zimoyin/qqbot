package com.github.zimoyin.qqbot.event.events.channel.guild

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import com.github.zimoyin.qqbot.event.events.channel.ChannelEvent
import com.github.zimoyin.qqbot.event.handler.NoneEventHandler
import com.github.zimoyin.qqbot.net.bean.GuildBean


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 频道事件
 */
@EventAnnotation.EventMetaType("Not_MetaType_GuildOperate")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
interface GuildOperate : ChannelEvent {
    override val metadataType: String
        get() = "Not_MetaType_GuildOperate"

    val guild: GuildBean

    override val channel: Channel
        get() = ChannelImpl.convert(botInfo,guild.id!!,null,guild.id)
}
