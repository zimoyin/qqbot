package com.github.zimoyin.qqbot.event.events.platform

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.event.events.Event
import com.github.zimoyin.qqbot.event.handler.NoneEventHandler

/**
 *
 * @author : zimo
 * @date : 2023/12/19
 *
 * 平台事件，该事件以及子事件多数只能在全局事件总线中进行传播，如果使用了Bot的本地监听是无法监听到大部分该事件以及子事件的
 * 由于事件的特殊性，该叶子事件全部是具体类，因此不需要事件处理器对事件进行构建 [github.zimoyin.annotation.EventAnnotation.EventHandler]
 */
@EventAnnotation.EventMetaType("Not_MetaType_PlatformEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
interface PlatformEvent : Event
