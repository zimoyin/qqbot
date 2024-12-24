package io.github.zimoyin.qqbot.event.events.paste

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.message.MessageAddPasteHandler
import io.github.zimoyin.qqbot.net.bean.message.MessageReaction

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 信息粘贴事件
 */
@EventAnnotation.EventMetaType("MESSAGE_REACTION_ADD")
@EventAnnotation.EventHandler(MessageAddPasteHandler::class)
data class MessageAddPasteEvent(
    override val metadata: String,
    override val metadataType: String = "MESSAGE_REACTION_ADD",
    override val botInfo: BotInfo,
    override val message: MessageReaction,
    override val eventID: String ="",
) : MessagePasteEvent
