package io.github.zimoyin.qqbot.event.supporter

import io.github.zimoyin.qqbot.event.events.Event

/**
 *
 * @author : zimo
 * @date : 2023/12/08
 * 元事件的事件类型与处理器映射
 */
data class MateEventMapping(
    val eventType: String,
    val eventCls: Class<out Event>,
    val eventHandler: Class<out AbsEventHandler<out Event>>,
)
