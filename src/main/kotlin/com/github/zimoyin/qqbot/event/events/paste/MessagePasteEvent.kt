package com.github.zimoyin.qqbot.event.events.paste

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.event.events.Event
import com.github.zimoyin.qqbot.net.bean.message.MessageReaction

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 信息粘贴事件
 */
@EventAnnotation.EventMetaType("Not_MetaType_MessagePasteEvent")
@EventAnnotation.EventHandler(ignore = true)
interface MessagePasteEvent : Event {
    val message: MessageReaction
}
