package com.github.zimoyin.qqbot.event.events.message.direct

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.event.events.message.MessageEvent
import com.github.zimoyin.qqbot.event.handler.message.MessageHandler

/**
 *
 * @author : zimo
 * @date : 2023/12/09
 */
@EventAnnotation.EventMetaType("Not_MetaType_PrivateMessageEvent")
@EventAnnotation.EventHandler(MessageHandler::class, true)
interface PrivateMessageEvent : MessageEvent
