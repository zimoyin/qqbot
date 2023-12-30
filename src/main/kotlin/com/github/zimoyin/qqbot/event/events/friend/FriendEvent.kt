package com.github.zimoyin.qqbot.event.events.friend

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.event.events.Event
import java.time.Instant

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("Not_MetaType_FriendEvent")
@EventAnnotation.EventHandler(ignore = true)
interface FriendEvent: Event {
    val friendID :String
    val timestamp : Instant
        get() = Instant.now()
}