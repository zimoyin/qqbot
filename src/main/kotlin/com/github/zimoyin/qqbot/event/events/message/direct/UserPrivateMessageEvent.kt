package com.github.zimoyin.qqbot.event.events.message.direct

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.contact.User
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.event.events.friend.FriendEvent
import com.github.zimoyin.qqbot.event.events.message.direct.PrivateMessageEvent
import com.github.zimoyin.qqbot.event.handler.message.UserPrivateMessageHandler
import java.time.Instant

/**
 *
 * @author : zimo
 * @date : 2023/12/12
 *   C2C_MESSAGE_CREATE 私信子事件 单聊
 */
@EventAnnotation.EventMetaType("C2C_MESSAGE_CREATE")
@EventAnnotation.EventHandler(UserPrivateMessageHandler::class)
class UserPrivateMessageEvent(
    override val metadataType: String = "C2C_MESSAGE_CREATE",
    override val metadata: String,
    override val msgID: String,
    override val windows: User,
    override val messageChain: MessageChain,
    override val sender: User,
    override val botInfo: BotInfo,

    override val friendID: String,
    override val timestamp: Instant,
    ) : FriendEvent, PrivateMessageEvent
