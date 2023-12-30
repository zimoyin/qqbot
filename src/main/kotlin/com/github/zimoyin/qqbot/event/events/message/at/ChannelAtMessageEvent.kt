package com.github.zimoyin.qqbot.event.events.message.at

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.bot.contact.User
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.event.events.message.ChannelMessageEvent
import com.github.zimoyin.qqbot.event.handler.message.ChannelAtMessageHandler

/**
 *
 * @author : zimo
 * @date : 2023/12/12
 *
 * TODO 文字子频道@机器人
 */
@EventAnnotation.EventMetaType("AT_MESSAGE_CREATE")
@EventAnnotation.EventHandler(ChannelAtMessageHandler::class)
class ChannelAtMessageEvent(
    override val metadata: String,
    override val metadataType: String,
    override val msgID: String,
    override val windows: Channel,
    override val messageChain: MessageChain,
    override val sender: User,
    override val botInfo: BotInfo,
    override val channel: Channel = windows,
) : ChannelMessageEvent, AtMessageEvent