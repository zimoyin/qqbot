package com.github.zimoyin.qqbot.event.events.channel

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.event.events.Event


/**
 *
 * @author : zimo
 * @date : 2023/12/08
 */

@EventAnnotation.EventMetaType("Not_MetaType_ChannelEvent")
@EventAnnotation.EventHandler(ignore = true)
interface ChannelEvent : Event {
    //TODO (Contact) Channel 属性
    val channel: Channel
}