package com.github.zimoyin.qqbot.event.events.paste

import com.github.zimoyin.qqbot.net.websocket.bean.MessageReaction
import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.handler.message.MessageDeletePasteHandler

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
) : MessagePasteEvent