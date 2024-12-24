package io.github.zimoyin.qqbot.event.events.group

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.event.events.Event
import java.util.*

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("Not_MetaType_GroupEvent")
@EventAnnotation.EventHandler(ignore = true)
interface GroupEvent : Event {
    val groupID :String
    val opMemberOpenid :String
        get() = "none"
    val timestamp : Date
        get() = Date()
}
