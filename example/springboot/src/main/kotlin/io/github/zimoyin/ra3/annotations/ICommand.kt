package io.github.zimoyin.ra3.annotations

import io.github.zimoyin.qqbot.event.events.message.MessageEvent

/**
 *
 * @author : zimo
 * @date : 2025/01/03
 */
interface ICommand<T : MessageEvent> {
    fun name(): String {
        return this::class.simpleName!!
    }

    fun enabled(): Boolean {
        return true
    }

    fun execute(event: T)
}