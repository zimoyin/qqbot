package com.github.zimoyin.qqbot.event.events.friend

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.handler.friend.CloseFriendBotHandler
import java.util.*

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 群管理员主动在机器人资料页操作关闭通知
 */
@EventAnnotation.EventMetaType("C2C_MSG_REJECT")
@EventAnnotation.EventHandler(CloseFriendBotHandler::class)
data class CloseFriendBotEvent(
    override val metadata: String,
    override val metadataType: String = "C2C_MSG_REJECT",
    override val botInfo: BotInfo,
    override val timestamp: Date,
    override val friendID: String,
    override val eventID: String ="",
) : FriendBotOperationEvent
