package com.github.zimoyin.qqbot.event.events.group

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.event.events.Event
import java.time.Instant

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
    val timestamp : Instant
        get() = Instant.now()
}