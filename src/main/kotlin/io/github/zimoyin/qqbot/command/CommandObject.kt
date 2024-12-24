package io.github.zimoyin.qqbot.command

import io.github.zimoyin.qqbot.event.events.message.MessageEvent

/**
 * @author : zimo
 * @date : 2024/05/10
 */
data class SimpleCommandObject(
    val commandSubject: String,
    var handle: ((parameters: SimpleCommandInfo?) -> Unit) = {},
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimpleCommandObject) return false

        if (commandSubject != other.commandSubject) return false

        return true
    }

    override fun hashCode(): Int {
        return commandSubject.hashCode()
    }
}

class SimpleCommandInfo(
    @JvmField
    val commandParams: String,
    @JvmField
    val event: MessageEvent,
)
