package com.github.zimoyin.qqbot.net.bean.message

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.zimoyin.qqbot.bot.message.EmojiType
import java.io.Serializable

data class MessageReaction(
    /**
     * 用户ID
     */
    @JsonProperty("user_id")
    val userId: String? = null,

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
     * 表态对象
     */
    @JsonProperty("target")
    val target: ReactionTarget? = null,

    /**
     * 表态所用表情
     */
    @JsonProperty("emoji")
    val mateEmoji: EmojiBean? = null,
) : Serializable {
    /**
     * 表态所用表情类型
     */
    @JsonIgnore
    fun getEmoji(): EmojiType {
        return mateEmoji?.id?.toInt()?.let { EmojiType.fromValue(it) }!!
    }
}

data class ReactionTarget(
    /**
     * 表态对象ID
     */
    @JsonProperty("id")
    val id: String? = null,

    /**
     * 表态对象类型，参考 [ReactionTargetType]
     */
    @JsonProperty("type")
    val mateType: String? = null,
) : Serializable

enum class ReactionTargetType(val value: Int, val description: String) : Serializable {
    MESSAGE(0, "消息"),
    THREAD(1, "帖子"),
    POST(2, "评论"),
    REPLY(3, "回复");

    companion object {
        fun fromValue(value: Int): ReactionTargetType? {
            return entries.firstOrNull { it.value == value }
        }
    }
}
