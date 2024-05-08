package com.github.zimoyin.qqbot.event.events.group.member

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.event.events.group.GroupEvent

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("Not_MetaType_GroupMemberUpdate")
@EventAnnotation.EventHandler(ignore = true)
interface GroupMemberUpdateEvent : GroupEvent
