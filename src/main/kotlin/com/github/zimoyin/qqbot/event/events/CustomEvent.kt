package com.github.zimoyin.qqbot.event.events

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.event.handler.NoneEventHandler

/**
 *
 * @author : zimo
 * @date : 2024/01/02
 */
@EventAnnotation.EventMetaType("Not_MetaType_CustomEvents")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
abstract class CustomEvent : Event {
  override val metadataType: String
    get() = this.javaClass.name

  override val metadata: String
    get() = this.javaClass.name
}
