package io.github.zimoyin.qqbot.event.events.friend

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.friend.AddFriendHandler
import java.util.*

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 用户添加机器人'好友'到消息列表
 */

@EventAnnotation.EventMetaType("FRIEND_ADD")
@EventAnnotation.EventHandler(AddFriendHandler::class)
data class AddFriendEvent(
    override val metadata: String,
    override val metadataType: String = "FRIEND_ADD",
    override val botInfo: BotInfo,
    override val timestamp :Date,
    override val friendID: String,
    override val eventID: String ="",
): FriendUpdateEvent
