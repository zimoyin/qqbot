package com.github.zimoyin.qqbot.event.events.message.direct

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.bot.contact.User
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.event.events.channel.ChannelEvent
import com.github.zimoyin.qqbot.event.handler.message.ChannelPrivateMessageHandler

/**
 *
 * @author : zimo
 * @date : 2023/12/11
 *
 * 频道的私信事件。私信机器人
 */
@EventAnnotation.EventMetaType("DIRECT_MESSAGE_CREATE")
@EventAnnotation.EventHandler(ChannelPrivateMessageHandler::class)
class ChannelPrivateMessageEvent(
    override val metadata: String,
    override val msgID: String,
    override val windows: Channel,
    override val messageChain: MessageChain,
    override val sender: User,
    override val botInfo: BotInfo,
    override val channel: Channel = windows,
) : ChannelEvent, PrivateMessageEvent {
    override val metadataType: String = "DIRECT_MESSAGE_CREATE"
}