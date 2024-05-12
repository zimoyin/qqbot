package com.github.zimoyin.qqbot.event.events.friend

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.handler.friend.DeleteFriendHandler
import java.util.*

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 用户删除机器人'好友'到消息列表
 */

@EventAnnotation.EventMetaType("FRIEND_DEL")
@EventAnnotation.EventHandler(DeleteFriendHandler::class)
data class DeleteFriendEvent(
    override val metadata: String,
    override val metadataType: String = "FRIEND_DEL",
    override val botInfo: BotInfo,
    override val timestamp : Date,
    override val friendID: String,
): FriendUpdateEvent
