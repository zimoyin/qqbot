package com.github.zimoyin.qqbot.event.events.revoke

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.contact.Contact
import com.github.zimoyin.qqbot.bot.contact.User
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.event.handler.message.ChannelPrivateMessageRevokeHandler

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
    override val messageChain: MessageChain,
    override val sender: User,
    override val metadataType: String = "DIRECT_MESSAGE_DELETE",
    override val operatorID: String,
) : MessageRevokeEvent