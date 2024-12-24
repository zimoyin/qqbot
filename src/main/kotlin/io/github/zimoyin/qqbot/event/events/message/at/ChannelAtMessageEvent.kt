package io.github.zimoyin.qqbot.event.events.message.at

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.ChannelUser
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.events.message.ChannelMessageEvent
import io.github.zimoyin.qqbot.event.handler.message.ChannelAtMessageHandler

/**
 *
 * @author : zimo
 * @date : 2023/12/12
 *
 *   文字子频道@机器人
 */
@EventAnnotation.EventMetaType("AT_MESSAGE_CREATE")
@EventAnnotation.EventHandler(ChannelAtMessageHandler::class)
class ChannelAtMessageEvent(
  override val metadata: String,
  override val metadataType: String,
  override val msgID: String,
  override val windows: Channel,
  override val messageChain: MessageChain,
  override val sender: ChannelUser,
  override val botInfo: BotInfo,
  override val channel: Channel = windows,
  override val eventID: String ="",
) : ChannelMessageEvent, AtMessageEvent
