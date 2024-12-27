package io.github.zimoyin.qqbot.event.handler.message

import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.ChannelPrivateUser
import io.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import io.github.zimoyin.qqbot.event.events.revoke.ChannelPrivateMessageRevokeEvent
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
class ChannelPrivateMessageRevokeHandler : AbsEventHandler<ChannelPrivateMessageRevokeEvent>() {
    override fun handle(payload: Payload): ChannelPrivateMessageRevokeEvent {
        val d = JSON.toJsonObject(payload.eventContent.toString())
        val message = JSON.toObject<Message>(d.getJsonObject("message"))
        val msgID = message.msgID!!
        val botInfo = BotInfo.create(payload.appID!!)
//        val sender = Sender.convert(botInfo, message)
        val sender = ChannelPrivateUser.convert(botInfo, message)
        val channel = ChannelImpl.convert(botInfo, message)

        return ChannelPrivateMessageRevokeEvent(
            metadata = payload.metadata,
            msgID = msgID,
            windows = channel,
            sender = sender,
            botInfo = botInfo,
            operatorID = d.getJsonObject("op_user").getString("id"),
            eventID = payload.eventID?:""
        )
    }
}
