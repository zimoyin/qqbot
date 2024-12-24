package io.github.zimoyin.qqbot.event.events.friend

import io.github.zimoyin.qqbot.annotation.EventAnnotation

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 * 用户添加/删除机器人'好友'到消息列表
 */
@EventAnnotation.EventMetaType("Not_MetaType_FriendUpdateEvent")
@EventAnnotation.EventHandler(ignore = true)
interface FriendUpdateEvent : FriendEvent
