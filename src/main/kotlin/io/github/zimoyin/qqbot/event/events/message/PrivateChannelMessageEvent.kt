package io.github.zimoyin.qqbot.event.events.message

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.ChannelPrivateUser
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.handler.message.PrivateChannelMessageHandler

/**
 *
 * @author : zimo
 * @date : 2023/12/12
 *
 * 用户在文字子频道内发送的所有聊天消息（私域）
 * 注意只能监听私有域的
 */
@EventAnnotation.EventMetaType("MESSAGE_CREATE")
@EventAnnotation.EventHandler(PrivateChannelMessageHandler::class)
class PrivateChannelMessageEvent(
    override val metadataType: String = "MESSAGE_CREATE",
    override val metadata: String,
    override val msgID: String,
    override val windows: Channel,
    override val messageChain: MessageChain,
    override val sender: ChannelPrivateUser,
    override val botInfo: BotInfo,
    override val channel: Channel = windows,
    override val eventID: String ="",
) : ChannelMessageEvent, MessageEvent
