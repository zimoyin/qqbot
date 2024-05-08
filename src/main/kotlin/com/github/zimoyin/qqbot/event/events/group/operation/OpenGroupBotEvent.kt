package com.github.zimoyin.qqbot.event.events.group.operation

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.handler.group.OpenGroupBotHandler
import java.time.Instant

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 群管理员主动在机器人资料页操作开启通知
 */
@EventAnnotation.EventMetaType("GROUP_MSG_RECEIVE")
@EventAnnotation.EventHandler(OpenGroupBotHandler::class)
data class OpenGroupBotEvent(
    override val metadata: String,
    override val metadataType: String = "GROUP_MSG_RECEIVE",
    override val botInfo: BotInfo,
    override val groupID: String,
    override val timestamp: Instant,
    override val opMemberOpenid: String,
) : GroupBotOperationEvent
