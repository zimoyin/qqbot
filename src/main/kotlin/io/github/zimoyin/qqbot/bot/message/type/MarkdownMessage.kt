package io.github.zimoyin.qqbot.bot.message.type

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import io.github.zimoyin.qqbot.net.bean.message.MessageMarkdownBean
import io.github.zimoyin.qqbot.net.bean.message.MessageMarkdownParam
import io.github.zimoyin.qqbot.utils.JSON
import org.intellij.lang.annotations.Language

/**
 * MD 类型的信息，如果你需要构建他，推荐使用 MessageMarkdown.toMessage() 方法
 */
data class MarkdownMessage(
    val markdown: MessageMarkdownBean,
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
    fun toMessageChain(eventID: String? = null, msgID: String? = null): MessageChain {
        return MessageChainBuilder(msgID).append(this).appendEventId(eventID).build()
    }

    companion object {

        @JvmStatic
        fun create(templateId: String, vararg params: MessageMarkdownParam): MessageMarkdownBean {
            return MessageMarkdownBean(templateId, arrayListOf(*params), null)
        }

        @JvmStatic
        @UntestedApi
        fun createOfJson(@Language("JSON") json: String): MessageMarkdownBean {
            return JSON.toObject<MessageMarkdownBean>(json)
        }

        @JvmOverloads
        @JvmStatic
        fun create(
            templateId: String,
            params: ArrayList<MessageMarkdownParam> = arrayListOf(),
            content: String? = null
        ): MessageMarkdownBean {
            return MessageMarkdownBean(templateId, params, content)
        }
    }
}
