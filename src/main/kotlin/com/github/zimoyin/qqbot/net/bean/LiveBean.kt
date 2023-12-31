package com.github.zimoyin.qqbot.net.bean

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
data class UserLive(
    /**
     * 频道ID
     */
    @JsonProperty("guild_id")
    val guildId: String? = null,

    /**
     * 子频道ID
     */
    @JsonProperty("channel_id")
    val channelId: String? = null,

    /**
     * 子频道类型 [ChannelType]
     */
    @JsonProperty("channel_type")
    val channelType: Int? = null,

    /**
     * 用户ID
     */
    @JsonProperty("user_id")
    val userId: String? = null,
) : Serializable {
    fun getChannelType(): UserLiveChannelType {
        return UserLiveChannelType.fromValue(channelType!!)!!
    }
}

// 注意：以下枚举类型需要自行定义
enum class UserLiveChannelType(val value: Int, val description: String) {
    AUDIO_VIDEO_CHANNEL(2, "音视频子频道"),
    LIVE_SUB_CHANNEL(5, "直播子频道");

    companion object {
        fun fromValue(value: Int): UserLiveChannelType? {
            return entries.firstOrNull { it.value == value }
        }
    }
}
