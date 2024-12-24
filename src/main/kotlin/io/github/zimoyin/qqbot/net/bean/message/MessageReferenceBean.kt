package io.github.zimoyin.qqbot.net.bean.message

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 引用消息对象
 */
data class MessageReference(
    /**
     * 需要引用回复的消息 id
     */
    @field:JsonProperty("message_id")
    val messageId: String? = null,

    /**
     * 是否忽略获取引用消息详情错误，默认否
     */
    @field:JsonProperty("ignore_get_message_error")
    val ignoreGetMessageError: Boolean? = false,
) : Serializable
