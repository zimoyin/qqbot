package io.github.zimoyin.qqbot.bot.message.type

class ReferenceMessage(val id: String) : MessageItem {
    override fun toStringType(): String {
        return "[ReferenceMessage:$id]"
    }
}
