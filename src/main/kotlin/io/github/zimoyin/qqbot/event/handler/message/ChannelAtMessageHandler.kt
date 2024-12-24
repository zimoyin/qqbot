package io.github.zimoyin.qqbot.event.handler.message

import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.ChannelUser
import io.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.events.message.at.ChannelAtMessageEvent
import io.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.net.bean.message.Message
import io.github.zimoyin.qqbot.utils.JSON

/**
 *
 * @author : zimo
 * @date : 2023/12/11
 *
 * 通用的消息处理器
 */
class ChannelAtMessageHandler : AbsEventHandler<ChannelAtMessageEvent>() {
  override fun handle(payload: Payload): ChannelAtMessageEvent {
    val message = JSON.toObject<Message>(payload.eventContent.toString())
    val info = BotInfo.create(payload.appID!!)

    return ChannelAtMessageEvent(
      msgID = message.msgID!!,
      windows = ChannelImpl.convert(info, message),
      messageChain = MessageChain.convert(message),
      sender = ChannelUser.convert(info, message),
      metadata = payload.metadata,
      metadataType = payload.eventType!!,
      botInfo = info,
        eventID = payload.eventID?:""
    )
  }
}
