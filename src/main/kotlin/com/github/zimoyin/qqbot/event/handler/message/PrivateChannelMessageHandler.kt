package com.github.zimoyin.qqbot.event.handler.message

import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import com.github.zimoyin.qqbot.bot.contact.ChannelUser
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.event.events.message.PrivateChannelMessageEvent
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import com.github.zimoyin.qqbot.net.bean.message.Message
import com.github.zimoyin.qqbot.net.bean.Payload
import com.github.zimoyin.qqbot.utils.JSON

/**
 *
 * @author : zimo
 * @date : 2023/12/11
 * 频道私信事件
 */
class PrivateChannelMessageHandler : AbsEventHandler<PrivateChannelMessageEvent>() {
  override fun handle(payload: Payload): PrivateChannelMessageEvent {
    val message = JSON.toObject<Message>(payload.eventContent.toString())
    val msgID = message.msgID!!
    val messageChain = MessageChain.convert(message)
    val botInfo = BotInfo.create(payload.appID!!)
    val channel = ChannelImpl.convert(botInfo, message)
//    val sender = Sender.convert(botInfo, message)
    val sender = ChannelUser.convert(botInfo, message)

    return PrivateChannelMessageEvent(
      metadataType = payload.eventType!!,
      metadata = payload.metadata,
      msgID = msgID,
      windows = channel,
      messageChain = messageChain,
      sender = sender,
      botInfo = botInfo,
    )
  }
}
