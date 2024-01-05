package com.github.zimoyin.qqbot.bot.message.type

import com.github.zimoyin.qqbot.net.bean.MessageMarkdown
import com.github.zimoyin.qqbot.utils.JSON

data class MarkdownMessage(val markdown: MessageMarkdown, val content: String = JSON.toJsonString(markdown)) : MessageItem {
    override fun toContent(): String {
        return content
    }

    override fun toStringType(): String {
        return "[MessageMarkdown:${markdown.templateId}]"
    }

    override fun toMetaContent(): String {
        return content
    }
}
