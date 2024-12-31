package io.github.zimoyin.qqbot.bot.message.type

import io.github.zimoyin.qqbot.bot.message.EmojiType

data class EmojiMessage(val id: String, val emojiType: EmojiType = EmojiType.fromValueID(id) ?: EmojiType.NULL) :
    MessageItem {
    override fun toContent(): String {
        return "/${emojiType.description}"
    }

    override fun toStringType(): String {
        return "[Emoji:$id]"
    }

    override fun toMetaContent(): String {
        return "<emoji:$id>"
    }

    companion object {
        @JvmStatic
        fun create(id: String): EmojiMessage {
            return EmojiMessage(id)
        }

        @JvmStatic
        fun create(emojiType: EmojiType): EmojiMessage {
            return EmojiMessage(emojiType.id.toString())
        }

    }
}
