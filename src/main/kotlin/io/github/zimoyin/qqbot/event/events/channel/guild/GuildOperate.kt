package io.github.zimoyin.qqbot.event.events.channel.guild

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import io.github.zimoyin.qqbot.event.events.channel.ChannelEvent
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler
import io.github.zimoyin.qqbot.net.bean.GuildBean


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
