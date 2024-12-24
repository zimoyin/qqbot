package io.github.zimoyin.qqbot.event.events.paste

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.message.MessageDeletePasteHandler
import io.github.zimoyin.qqbot.net.bean.message.MessageReaction

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 信息粘贴事件
 */
@EventAnnotation.EventMetaType("MESSAGE_REACTION_REMOVE")
@EventAnnotation.EventHandler(MessageDeletePasteHandler::class)
data class MessageDeletePasteEvent(
    override val metadata: String,
    override val metadataType: String = "MESSAGE_REACTION_REMOVE",
    override val botInfo: BotInfo,
    override val message: MessageReaction,
    override val eventID: String ="",
) : MessagePasteEvent
