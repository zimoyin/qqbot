package com.github.zimoyin.qqbot.event.events.channel.sub

import com.github.zimoyin.qqbot.net.bean.ChannelBean
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
