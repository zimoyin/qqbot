package io.github.zimoyin.qqbot.event.events.message.at

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Group
import io.github.zimoyin.qqbot.bot.contact.User
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.events.group.GroupEvent
import io.github.zimoyin.qqbot.event.handler.message.GroupAtMessageHandler
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
    override val eventID: String ="",
    override var msgSeq: Int=1,
) : GroupEvent, AtMessageEvent
