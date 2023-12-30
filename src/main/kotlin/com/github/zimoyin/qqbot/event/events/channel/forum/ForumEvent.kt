package com.github.zimoyin.qqbot.event.events.channel.forum

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.event.events.channel.ChannelEvent
import com.github.zimoyin.qqbot.event.handler.NoneEventHandler

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 论坛事件事件
 * TODO 该事件以及子事件未经任何测试
 */
@EventAnnotation.EventMetaType("Not_MetaType_ForumEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
interface ForumEvent : ChannelEvent {
    override val metadataType: String
        get() = "Not_MetaType_LiveRoomEvent"
}