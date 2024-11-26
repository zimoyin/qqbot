package com.github.zimoyin.qqbot.bot.message.type

import com.github.zimoyin.qqbot.net.bean.message.MessageMarkdown
import com.github.zimoyin.qqbot.utils.JSON

/**
 * MD 类型的信息，如果你需要构建他，推荐使用 MessageMarkdown.toMessage() 方法
 */
data class MarkdownMessage(
    val markdown: MessageMarkdown,
    val content: String = JSON.toJsonString(markdown),
) : MessageItem {
    init {
        require(!(markdown.templateId == null && markdown.content == null)) { "templateId and content is null" }
    }

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
