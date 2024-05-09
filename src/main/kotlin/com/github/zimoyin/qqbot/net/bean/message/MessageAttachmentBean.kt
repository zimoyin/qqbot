package com.github.zimoyin.qqbot.net.bean.message

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.net.URL

/**
 * 附件
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageAttachment(
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
    fun getURL(): String {
        return URL("https://$uri").toString()
    }
}
