package com.github.zimoyin.qqbot.bot.message.type

import com.github.zimoyin.qqbot.net.bean.message.MessageEmbed
import com.github.zimoyin.qqbot.utils.JSON

data class EmbedMessage(val embed: MessageEmbed, val content: String = JSON.toJsonString(embed)) : MessageItem {
    override fun toContent(): String {
        return content
    }

    override fun toStringType(): String {
        return "[MessageEmbed:${embed.title}]"
    }

    override fun toMetaContent(): String {
        return content
    }
}
