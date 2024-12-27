package io.github.zimoyin.qqbot.event.handler.message

import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Contact
import io.github.zimoyin.qqbot.bot.contact.GroupImpl
import io.github.zimoyin.qqbot.bot.contact.PrivateFriend
import io.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import io.github.zimoyin.qqbot.event.events.InteractionEvent
import io.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.net.bean.message.InteractionEventData
import io.github.zimoyin.qqbot.utils.JSON

/**
 *
 * @author : zimo
 * @date : 2023/12/11
 * 频道私信事件
 */
class InteractionEventHandler : AbsEventHandler<InteractionEvent>() {
    override fun handle(payload: Payload): InteractionEvent {
        val data = JSON.toObject<InteractionEventData>(payload.eventContent.toString())
        val botInfo = BotInfo.create(payload.appID!!)

        return InteractionEvent(
            metadataType = payload.eventType!!,
            metadata = payload.metadata,
            botInfo = botInfo,
            eventID = payload.eventID ?: "",
            data = data,
            windows = getWindows(botInfo, data)
        )
    }

    private fun getWindows(info: BotInfo, data: InteractionEventData): Contact {
        return when {
            data.groupOpenid != null -> {
                //群聊
                GroupImpl.convert(info, data.groupOpenid)
            }

            data.guildId != null -> {
                //频道
                ChannelImpl.convert(info, data.guildId,data.channelId,null)
            }

            data.userOpenid != null -> {
                // 私聊
                PrivateFriend.convert(info, data.userOpenid)
            }
            else -> {
                throw RuntimeException("未知的窗口类型")
            }
        }
    }
}
