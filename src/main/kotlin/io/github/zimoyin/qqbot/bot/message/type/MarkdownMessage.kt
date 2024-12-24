package io.github.zimoyin.qqbot.bot.message.type

import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import io.github.zimoyin.qqbot.net.bean.message.MessageMarkdown
import io.github.zimoyin.qqbot.net.bean.message.MessageMarkdownParam
import io.github.zimoyin.qqbot.utils.JSON

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

    @JvmOverloads
    fun toMessageChain(eventID: String? = null,msgID: String? = null): MessageChain {
        return MessageChainBuilder(msgID).append(this).appendEventId(eventID).build()
    }

    companion object{
        @JvmStatic
        fun builder(templateId: String, params: ArrayList<MessageMarkdownParam>): MarkdownMessage {
            return MarkdownMessage(MessageMarkdown.create(templateId, params))
        }

        @JvmStatic
        fun builder(templateId: String): MarkdownMessage {
            return MarkdownMessage(MessageMarkdown.create(templateId))
        }

        @JvmStatic
        fun builder(templateId: String, vararg params: MessageMarkdownParam): MarkdownMessage {
            return MarkdownMessage(MessageMarkdown.create(templateId, arrayListOf<MessageMarkdownParam>().apply {
                addAll(params)
            }))
        }
    }
}
