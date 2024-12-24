package io.github.zimoyin.qqbot.event.events

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler

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
