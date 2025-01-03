package io.github.zimoyin.qqbot.net.bean.message

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.zimoyin.qqbot.LocalLogger
import java.io.Serializable
import java.net.URI

/**
 * 附件
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageAttachment(
    /**
     * 协议
     */
    @field:JsonIgnore
    val protocol: String = "https",

    /**
     * 下载地址
     */
    @field:JsonProperty("url")
    val uri: String? = null,

    /**
     * 文件类型
     */
    @field:JsonProperty("content_type")
    val contentType: String? = null,

    /**
     * 文件名
     */
    @field:JsonProperty("filename")
    val filename: String? = null,

    /**
     * 图片高度
     */
    @field:JsonProperty("height")
    val height: Int? = null,

    /**
     * 文件ID
     */
    @field:JsonProperty("id")
    val id: String? = null,

    /**
     * 文件大小
     */
    @field:JsonProperty("size")
    val size: Long? = null,

    /**
     * 图片宽度
     */
    @field:JsonProperty("width")
    val width: Int? = null,
) : Serializable {

    @JsonIgnore
    fun getURL(): String? {
        val logger = LocalLogger(MessageAttachment::class.java)
        if (uri == null) return null
        if (protocol != "https") logger.warn("附件协议不是https: $protocol://$uri")
        return URI.create("$protocol://$uri").toURL().toString()
    }
}
