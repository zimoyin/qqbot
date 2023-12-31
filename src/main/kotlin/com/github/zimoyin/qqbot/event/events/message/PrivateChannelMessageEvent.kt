package com.github.zimoyin.qqbot.event.events.message

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.bot.contact.ChannelUser
import com.github.zimoyin.qqbot.bot.contact.User
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.event.handler.message.PrivateChannelMessageHandler

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
  override val sender: ChannelUser,
  override val botInfo: BotInfo,
  override val channel: Channel = windows
) : ChannelMessageEvent, MessageEvent
