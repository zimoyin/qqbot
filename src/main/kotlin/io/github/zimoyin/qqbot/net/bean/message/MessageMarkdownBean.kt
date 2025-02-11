package io.github.zimoyin.qqbot.net.bean.message

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.message.type.MarkdownMessage
import io.github.zimoyin.qqbot.utils.JSON
import org.intellij.lang.annotations.Language
import java.io.Serializable

/**
 * Markdown消息
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageMarkdownBean(
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
    val params: ArrayList<MessageMarkdownParam> = arrayListOf(),

    /**
     * 原生 Markdown 内容，与 template_id 和 params 参数互斥
     */
    @field:JsonProperty("content")
    var content: String? = null,
) : Serializable {

    @JsonIgnore
    fun appendParam(value: MessageMarkdownParam): MessageMarkdownBean {
        return params.apply { add(value) }.let { this }
    }


    @JsonIgnore
    fun appendParam(key: String, vararg value: String): MessageMarkdownBean {
        return params.apply { add(MessageMarkdownParam(key, value.toList())) }.let { this }
    }

    @JsonIgnore
    fun appendParams(vararg value: MessageMarkdownParam): MessageMarkdownBean {
        return params.apply { addAll(value) }.let { this }
    }

    /**
     * 是否是主动信息
     * 默认是主动信息，对于机器人 dua 小于2000的不能使用被动信息
     */
    @JsonIgnore
    var _isSrvSendMsg: Boolean = true

    @JsonIgnore
    fun setSrvSendMsg(isSrvSendMsg: Boolean): MessageMarkdownBean {
        this._isSrvSendMsg = isSrvSendMsg
        return this
    }

    @Deprecated("please use build()")
    @JsonIgnore
    fun toMessage(): MarkdownMessage {
        return MarkdownMessage(this)
    }

    @JsonIgnore
    fun build(): MarkdownMessage {
        return MarkdownMessage(this)
    }

    @JsonIgnore
    fun toJson(): String {
        return JSON.toJsonString(this)
    }

    companion object {
        /**
         * @link https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/type/markdown.html#%E5%8F%91%E9%80%81%E6%96%B9%E5%BC%8F
         */
        @JsonIgnore
        @JvmStatic
        @UntestedApi
        fun createOfJson(@Language("JSON") json: String): MessageMarkdownBean {
            return JSON.toObject<MessageMarkdownBean>(json)
        }

        @JsonIgnore
        @JvmStatic
        fun create(templateId: String, params: ArrayList<MessageMarkdownParam>): MessageMarkdownBean {
            return MessageMarkdownBean(templateId, params, null)
        }

        @JsonIgnore
        @JvmStatic
        fun create(templateId: String): MessageMarkdownBean {
            return MessageMarkdownBean(templateId, content = null)
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
        @JvmStatic
        fun create(key: String, value: String): MessageMarkdownParam {
            return MessageMarkdownParam(key, listOf(value))
        }

        @JsonIgnore
        @JvmStatic
        fun create(key: String, vararg value: String): MessageMarkdownParam {
            return MessageMarkdownParam(key, value.toList())
        }

        @JsonIgnore
        @JvmStatic
        fun create(@Language("JSON") json: String): MessageMarkdownParam {
            return JSON.toObject<MessageMarkdownParam>(json)
        }
    }
}
