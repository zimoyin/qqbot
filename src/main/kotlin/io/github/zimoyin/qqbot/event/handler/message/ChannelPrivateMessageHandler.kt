package io.github.zimoyin.qqbot.event.handler.message

import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.ChannelUser
import io.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.events.message.direct.ChannelPrivateMessageEvent
import io.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.net.bean.message.Message
import io.github.zimoyin.qqbot.utils.JSON

/**
 *
 * @author : zimo
 * @date : 2023/12/11
 * 频道私信事件
 */
class ChannelPrivateMessageHandler : AbsEventHandler<ChannelPrivateMessageEvent>() {
  override fun handle(payload: Payload): ChannelPrivateMessageEvent {
    val message = JSON.toObject<Message>(payload.eventContent.toString())
    val msgID = message.msgID!!
    val messageChain = MessageChain.convert(message)
    val botInfo = BotInfo.create(payload.appID!!)
    val channel = ChannelImpl.convert(botInfo, message)
//      val sender = Sender.convert(botInfo,message)
    val sender = ChannelUser.convert(botInfo, message)

    return ChannelPrivateMessageEvent(
      metadata = payload.metadata,
      msgID = msgID,
      windows = channel,
      messageChain = messageChain,
      sender = sender,
      botInfo = botInfo,
        eventID = payload.eventID?:""
    )
  }
}
