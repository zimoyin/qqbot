package io.github.zimoyin.qqbot.event.events.group.operation

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.event.events.group.GroupEvent
import io.github.zimoyin.qqbot.event.events.operation.BotOperationEvent

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("Not_MetaType_GroupBotOperationEvent")
@EventAnnotation.EventHandler(ignore = true)
interface GroupBotOperationEvent : GroupEvent, BotOperationEvent
