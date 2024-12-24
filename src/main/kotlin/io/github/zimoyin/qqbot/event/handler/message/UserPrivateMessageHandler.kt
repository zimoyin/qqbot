package io.github.zimoyin.qqbot.event.handler.message

import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.PrivateFriend
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.events.message.direct.UserPrivateMessageEvent
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
class UserPrivateMessageHandler : AbsEventHandler<UserPrivateMessageEvent>() {
    override fun handle(payload: Payload): UserPrivateMessageEvent {
        val message = JSON.toObject<Message>(payload.eventContent.toString())
        val msgID = message.msgID!!
        val messageChain = MessageChain.convert(message)
        val botInfo = BotInfo.create(payload.appID!!)
        val sender = PrivateFriend.convert(botInfo,message)

        return UserPrivateMessageEvent(
            metadata = payload.metadata,
            msgID = msgID,
            windows = sender,
            messageChain = messageChain,
            sender = sender,
            botInfo = botInfo,
            friendID = sender.id,
            timestamp = messageChain.timestamp,
            eventID = payload.eventID?:""
        )
    }
}
