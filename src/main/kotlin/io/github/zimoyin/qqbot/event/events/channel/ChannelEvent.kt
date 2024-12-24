package io.github.zimoyin.qqbot.event.events.channel

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.event.events.Event


/**
 *
 * @author : zimo
 * @date : 2023/12/08
 */

@EventAnnotation.EventMetaType("Not_MetaType_ChannelEvent")
@EventAnnotation.EventHandler(ignore = true)
interface ChannelEvent : Event {
    val channel: Channel
}
