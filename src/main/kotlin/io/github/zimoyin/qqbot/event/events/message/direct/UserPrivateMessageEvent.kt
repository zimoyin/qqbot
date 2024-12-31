package io.github.zimoyin.qqbot.event.events.message.direct

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.PrivateFriend
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.events.friend.FriendEvent
import io.github.zimoyin.qqbot.event.handler.message.UserPrivateMessageHandler
import java.util.*

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
    override val windows: PrivateFriend,
    override val messageChain: MessageChain,
    override val sender: PrivateFriend,
    override val botInfo: BotInfo,

    override val friendID: String,
    override val timestamp: Date,
    override val eventID: String ="",
    override var msgSeq: Int = 1,
    ) : FriendEvent, PrivateMessageEvent
