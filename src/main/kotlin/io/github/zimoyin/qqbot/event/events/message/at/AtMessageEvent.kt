package io.github.zimoyin.qqbot.event.events.message.at

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import io.github.zimoyin.qqbot.event.handler.message.MessageHandler

/**
 *
 * @author : zimo
 * @date : 2023/12/09
 */
@EventAnnotation.EventMetaType("Not_MetaType_AtMessageEvent")
@EventAnnotation.EventHandler(MessageHandler::class, true)
interface AtMessageEvent: MessageEvent
