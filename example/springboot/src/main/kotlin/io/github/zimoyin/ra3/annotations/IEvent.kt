package io.github.zimoyin.ra3.annotations

import io.github.zimoyin.qqbot.event.events.Event

/**
 *
 * @author : zimo
 * @date : 2025/01/03
 */
interface IEvent<T : Event> {
    fun enabled(): Boolean {
        return true
    }

    fun execute(event: T)
}