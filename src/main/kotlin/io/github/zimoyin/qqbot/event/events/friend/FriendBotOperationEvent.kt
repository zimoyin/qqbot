package io.github.zimoyin.qqbot.event.events.friend

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.event.events.operation.BotOperationEvent

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("Not_MetaType_FriendBotOperationEvent")
@EventAnnotation.EventHandler(ignore = true)
interface FriendBotOperationEvent : FriendEvent,BotOperationEvent
