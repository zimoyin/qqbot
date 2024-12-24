package io.github.zimoyin.qqbot.event.events.revoke

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Contact
import io.github.zimoyin.qqbot.bot.contact.User
import io.github.zimoyin.qqbot.event.handler.message.ChannelMessageRevokeHandler

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 频道里信息撤回
 */
@EventAnnotation.EventMetaType("MESSAGE_DELETE")
@EventAnnotation.EventHandler(ChannelMessageRevokeHandler::class, true)
data class ChannelMessageRevokeEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val msgID: String,
    override val windows: Contact,
    override val sender: User,
    override val metadataType: String = "MESSAGE_DELETE",
    override val operatorID: String,
    override val eventID: String ="",
) : MessageRevokeEvent
