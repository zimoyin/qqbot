package com.github.zimoyin.qqbot.event.handler.message

import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.contact.PrivateFriend
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.event.events.message.direct.UserPrivateMessageEvent
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import com.github.zimoyin.qqbot.net.bean.Payload
import com.github.zimoyin.qqbot.net.bean.message.Message
import com.github.zimoyin.qqbot.utils.JSON

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
