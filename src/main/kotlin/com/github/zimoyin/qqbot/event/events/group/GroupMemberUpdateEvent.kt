package com.github.zimoyin.qqbot.event.events.group

import com.github.zimoyin.qqbot.annotation.EventAnnotation

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("Not_MetaType_GroupMemberUpdate")
@EventAnnotation.EventHandler(ignore = true)
interface GroupMemberUpdateEvent : GroupEvent