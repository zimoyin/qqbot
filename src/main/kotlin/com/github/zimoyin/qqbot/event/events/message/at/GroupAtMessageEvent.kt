package com.github.zimoyin.qqbot.event.events.message.at

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.contact.Group
import com.github.zimoyin.qqbot.bot.contact.User
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.event.events.group.GroupEvent
import com.github.zimoyin.qqbot.event.handler.message.GroupAtMessageHandler
import java.util.*

/**
 *
 * @author : zimo
 * @date : 2023/12/12
 *
 * 群@机器人
 */
@EventAnnotation.EventMetaType("GROUP_AT_MESSAGE_CREATE")
@EventAnnotation.EventHandler(GroupAtMessageHandler::class)
class GroupAtMessageEvent(
    override val metadataType: String = "GROUP_AT_MESSAGE_CREATE",
    override val metadata: String,
    override val msgID: String,
    override val windows: Group,
    override val messageChain: MessageChain,
    override val sender: User,
    override val botInfo: BotInfo,

    override val groupID: String,
    override val timestamp: Date,
    override val opMemberOpenid: String,
) : GroupEvent, AtMessageEvent
