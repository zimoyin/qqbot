package com.github.zimoyin.qqbot.event.events.group.member

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.handler.group.AddGroupHandler
import java.util.*

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 机器人加入群聊
 */

@EventAnnotation.EventMetaType("GROUP_ADD_ROBOT")
@EventAnnotation.EventHandler(AddGroupHandler::class)
data class AddGroupEvent(
    override val metadata: String,
    override val metadataType: String = "GROUP_ADD_ROBOT",
    override val botInfo: BotInfo,
    override val groupID :String,
    override val timestamp :Date,
    override val opMemberOpenid :String,
): GroupMemberUpdateEvent
