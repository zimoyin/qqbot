package com.github.zimoyin.qqbot.event.events.group.member

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.handler.group.ExitGroupHandler
import java.time.Instant

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 机器人推出群聊
 */

@EventAnnotation.EventMetaType("GROUP_DEL_ROBOT")
@EventAnnotation.EventHandler(ExitGroupHandler::class)
data class ExitGroupEvent(
    override val metadata: String,
    override val metadataType: String = "GROUP_DEL_ROBOT",
    override val botInfo: BotInfo,
    override val groupID :String,
    override val timestamp :Instant,
    override val opMemberOpenid :String,
): GroupMemberUpdateEvent
