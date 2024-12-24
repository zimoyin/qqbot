package io.github.zimoyin.qqbot.event.handler.message

import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.events.paste.MessageAddPasteEvent
import io.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.net.bean.message.MessageReaction
import io.github.zimoyin.qqbot.utils.JSON

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
class MessageAddPasteHandler : AbsEventHandler<MessageAddPasteEvent>() {
    override fun handle(payload: Payload): MessageAddPasteEvent {
        val msg = JSON.toObject<MessageReaction>(payload.eventContent.toString())
        return MessageAddPasteEvent(
            metadata = payload.metadata,
            botInfo = BotInfo.create(payload.appID!!),
            message = msg,
            eventID = payload.eventID?:""
        )
    }
}
