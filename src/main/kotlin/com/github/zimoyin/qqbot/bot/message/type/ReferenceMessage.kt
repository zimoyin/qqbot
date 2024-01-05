package com.github.zimoyin.qqbot.bot.message.type

data class ReferenceMessage(val id: String) : MessageItem {
    override fun toStringType(): String {
        return "[ReferenceMessage:$id]"
    }
}
