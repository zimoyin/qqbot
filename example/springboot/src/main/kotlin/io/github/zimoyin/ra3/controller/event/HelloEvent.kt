package io.github.zimoyin.ra3.controller.event

import io.github.zimoyin.ra3.annotations.EventHandle
import io.github.zimoyin.ra3.annotations.IEvent
import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import io.github.zimoyin.qqbot.event.events.message.at.AtMessageEvent

/**
 *
 * @author : zimo
 * @date : 2025/01/03
 */
@EventHandle(executeMethod = "test",event = AtMessageEvent::class)
class HelloEvent : IEvent<MessageEvent> {
    override fun execute(event: MessageEvent) {
        event.reply("hello HelloEvent.execute")
    }

    fun test(event: AtMessageEvent) {
        event.reply("hello HelloEvent.test")
    }

    @EventHandle(event = AtMessageEvent::class)
    fun test3(event: AtMessageEvent) {
        event.reply("hello HelloEvent.test3")
    }


}