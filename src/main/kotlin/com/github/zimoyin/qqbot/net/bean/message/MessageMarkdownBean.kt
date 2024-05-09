package com.github.zimoyin.qqbot.net.bean.message

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.zimoyin.qqbot.bot.message.type.MarkdownMessage
import com.github.zimoyin.qqbot.utils.JSON
import org.intellij.lang.annotations.Language
import java.io.Serializable

/**
 * Markdown消息
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageMarkdown(
    /**
     * Markdown 模板 id
     * 如果你确信你有权限不需要申请模板可以为 null
     */
//    @field:JsonProperty("template_id")
    @field:JsonProperty("custom_template_id")
    val templateId: String? = null,

    /**
     * Markdown 模板参数
     */
    @field:JsonProperty("params")
    val params: List<MessageMarkdownParam>? = null,

    /**
     * 原生 Markdown 内容，与 template_id 和 params 参数互斥
     */
    @field:JsonProperty("content")
    val content: String? = null,
) : Serializable {
    @JsonIgnore
    fun toMessage(): MarkdownMessage {
        return MarkdownMessage(this)
    }

    @JsonIgnore
    fun toJson(): String {
        return JSON.toJsonString(this)
    }

    companion object {

        @JsonIgnore
        fun create(@Language("JSON") json: String): MessageMarkdown {
            return JSON.toObject<MessageMarkdown>(json)
        }
    }
}

/**
 * Markdown消息参数
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageMarkdownParam(
    /**
     * Markdown 模板 key
     */
    @field:JsonProperty("key")
    val key: String? = null,

    /**
     * Markdown 模板 key 对应的 values
     * 列表长度大小为 1 代表单 value 值，长度大于 1 则为列表类型的参数 values 传参数
     */
    @field:JsonProperty("values")
    val values: List<String>? = null,
) : Serializable {

    @JsonIgnore
    fun add(value: MessageMarkdownParam): ArrayList<MessageMarkdownParam> = arrayListOf(this).apply {
        add(value)
    }

    companion object {
        @JsonIgnore
        fun create(key: String, value: Any): MessageMarkdownParam {
            return MessageMarkdownParam(key, listOf(value.toString()))
        }

        @JsonIgnore
        fun create(@Language("JSON") json: String): MessageMarkdownParam {
            return JSON.toObject<MessageMarkdownParam>(json)
        }
    }
}
