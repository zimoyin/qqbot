package io.github.zimoyin.ra3.controller.event

import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import io.github.zimoyin.qqbot.event.events.message.at.AtMessageEvent
import io.github.zimoyin.ra3.annotations.EventHandle
import io.github.zimoyin.ra3.event.VirtualMessageEvent
import org.springframework.stereotype.Component

/**
 *
 * @author : zimo
 * @date : 2025/01/10
 */
@Component
class TestEventHandler {

    @EventHandle(event = MessageEvent::class)
    fun hello(event: MessageEvent) {
        println("事件: $event")
    }
}