package io.github.zimoyin.qqbot.net.bean.message

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 定义精华消息对象（PinsMessage）数据类，提供默认值 null
 */
data class PinsMessageBean(
    /**
     * 频道ID
     */
    @field:JsonProperty("guild_id")
    val guildID: String? = null,

    /**
     * 子频道ID
     */
    @field:JsonProperty("channel_id")
    val channelID: String? = null,

    /**
     * 子频道内精华消息ID数组
     */
    @field:JsonProperty("message_ids")
    val messageIDs: List<String>? = null,
) : Serializable
