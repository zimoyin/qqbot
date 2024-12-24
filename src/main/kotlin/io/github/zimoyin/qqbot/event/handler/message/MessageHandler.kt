package io.github.zimoyin.qqbot.event.handler.message

import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Contact
import io.github.zimoyin.qqbot.bot.contact.GroupImpl
import io.github.zimoyin.qqbot.bot.contact.Sender
import io.github.zimoyin.qqbot.bot.contact.User
import io.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.events.message.MessageEvent
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
class MessageHandler : AbsEventHandler<MessageEvent>() {
    override fun handle(payload: Payload): MessageEvent {
        val message = JSON.toObject<Message>(payload.eventContent.toString())
        val info = BotInfo.create(payload.appID!!)

        return object : MessageEvent {
            override val msgID: String = message.msgID!!
            override val windows: Contact = getWindows(info, message)
            override val messageChain: MessageChain = MessageChain.convert(message)
            override val botInfo: BotInfo = info
            override val sender: User = Sender.convert(botInfo, message)
            override val metadata: String = payload.metadata
            override val metadataType: String = payload.eventType!!
            override val eventID: String = payload.eventID?:""
        }
    }

    companion object {
        fun getWindows(info: BotInfo, message: Message): Contact {
            return when {
                message.groupID != null -> {
                    //群聊
                    GroupImpl.convert(info, message)
                }

                message.srcGuildID != null -> {
                    //频道私聊
                    ChannelImpl.convert(info, message)
                }

                message.channelID != null -> {
                    //频道
                    ChannelImpl.convert(info, message)
                }

                else -> {
                    //朋友私聊
                    TODO("Not implemented")
                }
            }
        }
    }
}
