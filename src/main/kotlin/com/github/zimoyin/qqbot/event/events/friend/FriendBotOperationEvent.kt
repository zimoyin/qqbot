package com.github.zimoyin.qqbot.event.events.friend

import com.github.zimoyin.qqbot.annotation.EventAnnotation

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("Not_MetaType_FriendBotOperationEvent")
@EventAnnotation.EventHandler(ignore = true)
interface FriendBotOperationEvent : FriendEvent