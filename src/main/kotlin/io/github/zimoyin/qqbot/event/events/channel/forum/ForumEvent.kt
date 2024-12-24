package io.github.zimoyin.qqbot.event.events.channel.forum

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.event.events.channel.ChannelEvent
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 论坛事件事件
 */
@EventAnnotation.EventMetaType("Not_MetaType_ForumEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
interface ForumEvent : ChannelEvent {
    override val metadataType: String
        get() = "Not_MetaType_LiveRoomEvent"
}
