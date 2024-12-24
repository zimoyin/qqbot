package io.github.zimoyin.qqbot.event.events.revoke

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Contact
import io.github.zimoyin.qqbot.bot.contact.User
import io.github.zimoyin.qqbot.event.handler.message.ChannelPrivateMessageRevokeHandler

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 频道私信里信息撤回
 */
@EventAnnotation.EventMetaType("DIRECT_MESSAGE_DELETE")
@EventAnnotation.EventHandler(ChannelPrivateMessageRevokeHandler::class, true)
data class ChannelPrivateMessageRevokeEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val msgID: String,
    override val windows: Contact,
    override val sender: User,
    override val metadataType: String = "DIRECT_MESSAGE_DELETE",
    override val operatorID: String,
    override val eventID: String ="",
) : MessageRevokeEvent
