package com.github.zimoyin.qqbot.event.events.channel.live

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.event.events.channel.ChannelEvent
import com.github.zimoyin.qqbot.event.handler.NoneEventHandler
import com.github.zimoyin.qqbot.net.bean.UserLive


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 频道的音视频直播事件
 */
@EventAnnotation.EventMetaType("Not_MetaType_LiveRoomEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
interface LiveRoomEvent : ChannelEvent {
    override val metadataType: String
        get() = "Not_MetaType_LiveRoomEvent"

    val live: UserLive
}
