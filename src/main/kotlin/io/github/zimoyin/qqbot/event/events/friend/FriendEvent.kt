package io.github.zimoyin.qqbot.event.events.friend

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.event.events.Event
import java.util.*

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("Not_MetaType_FriendEvent")
@EventAnnotation.EventHandler(ignore = true)
interface FriendEvent: Event {
    val friendID :String
    val timestamp : Date
        get() = Date()
}
