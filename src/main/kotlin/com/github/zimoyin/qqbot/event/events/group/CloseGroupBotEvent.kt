package com.github.zimoyin.qqbot.event.events.group

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.handler.group.CloseGroupBotHandler
import java.time.Instant

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 群管理员主动在机器人资料页操作关闭通知
 */
@EventAnnotation.EventMetaType("GROUP_MSG_REJECT")
@EventAnnotation.EventHandler(CloseGroupBotHandler::class)
data class CloseGroupBotEvent(
    override val metadata: String,
    override val metadataType: String = "GROUP_MSG_REJECT",
    override val botInfo: BotInfo,
    override val groupID: String,
    override val timestamp: Instant,
    override val opMemberOpenid: String,
) : GroupBotOperationEvent