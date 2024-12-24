package io.github.zimoyin.qqbot.net.bean.message

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * Embed字段数据
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageEmbed(
    /**
     * 标题
     */
    @field:JsonProperty("title")
    val title: String? = null,

    /**
     * 消息弹窗内容
     */
    @field:JsonProperty("prompt")
    val prompt: String? = null,

    /**
     * 缩略图
     */
    @field:JsonProperty("thumbnail")
    val thumbnail: MessageEmbedThumbnail? = null,

    /**
     * Embed字段数据
     */
    @field:JsonProperty("fields")
    val fields: List<MessageEmbedField>? = null,
) : Serializable

/**
 * 缩略图
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageEmbedThumbnail(
    /**
     * 图片地址
     */
    @field:JsonProperty("url")
    val url: String? = null,
) : Serializable

/**
 * Embed字段数据
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageEmbedField(
    /**
     * 字段名
     */
    @field:JsonProperty("name")
    val name: String? = null,
) : Serializable
