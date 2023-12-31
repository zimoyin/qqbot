package com.github.zimoyin.qqbot.event.handler.message

import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.contact.GroupImpl
import com.github.zimoyin.qqbot.bot.contact.Sender
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.event.events.message.at.GroupAtMessageEvent
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import com.github.zimoyin.qqbot.net.bean.Message

import com.github.zimoyin.qqbot.utils.JSON
import com.github.zimoyin.qqbot.net.bean.Payload
/**
 *
 * @author : zimo
 * @date : 2023/12/11
 * 频道私信事件
 */
class GroupAtMessageHandler : AbsEventHandler<GroupAtMessageEvent>() {
    override fun handle(payload: Payload): GroupAtMessageEvent {
        val message = JSON.toObject<Message>(payload.eventContent.toString())
        val msgID = message.msgID!!
        val messageChain = MessageChain.convert(message)
        val botInfo = BotInfo.create(payload.appID!!)
        val sender = Sender.convert(botInfo,message)
        val group = GroupImpl.convert(botInfo,message)

        return GroupAtMessageEvent(
            metadata = payload.metadata,
            msgID = msgID,
            windows = group,
            messageChain = messageChain,
            sender = sender,
            botInfo = botInfo,
            groupID = group.id,
            timestamp = message.timestamp!!,
            opMemberOpenid = sender.id
        )
    }
}
