package io.github.zimoyin.qqbot.event.events.channel.sub

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import io.github.zimoyin.qqbot.event.events.channel.ChannelEvent
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler
import io.github.zimoyin.qqbot.net.bean.ChannelBean


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 子频道事件
 */
@EventAnnotation.EventMetaType("Not_MetaType_SubChannelEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
interface SubChannelEvent : ChannelEvent {
    override val metadataType: String
        get() = "Not_MetaType_SubChannelEvent"

    val channelBean: ChannelBean

    override val channel: Channel
        get() = ChannelImpl.convert(botInfo,channelBean.guildID,channelBean.channelID,channelBean.channelID)
}
