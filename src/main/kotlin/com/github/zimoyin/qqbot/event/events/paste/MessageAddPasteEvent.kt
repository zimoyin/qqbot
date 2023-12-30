package com.github.zimoyin.qqbot.event.events.paste

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.handler.message.MessageAddPasteHandler
import com.github.zimoyin.qqbot.net.websocket.bean.MessageReaction

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
) : MessagePasteEvent