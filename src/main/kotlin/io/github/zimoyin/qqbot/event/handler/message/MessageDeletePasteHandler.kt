package io.github.zimoyin.qqbot.event.handler.message

import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.events.paste.MessageDeletePasteEvent
import io.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.net.bean.message.MessageReaction
import io.github.zimoyin.qqbot.utils.JSON

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
class MessageDeletePasteHandler : AbsEventHandler<MessageDeletePasteEvent>() {
    override fun handle(payload: Payload): MessageDeletePasteEvent {
        val msg = JSON.toObject<MessageReaction>(payload.eventContent.toString())
        return MessageDeletePasteEvent(
            metadata = payload.metadata,
            botInfo = BotInfo.create(payload.appID!!),
            message = msg,
            eventID = payload.eventID?:""
        )
    }
}
