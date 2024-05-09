package com.github.zimoyin.qqbot.net.bean.message

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.zimoyin.qqbot.bot.message.EmojiType
import java.io.Serializable

data class EmojiBean(
    /**
     * 表情ID，系统表情使用数字为ID，emoji使用emoji本身为id，参考 Emoji 列表
     */
    @JsonProperty("id")
    val id: String? = null,

    /**
     * 表情类型，参考 [EmojiType]
     */
    @JsonProperty("type")
    val type: EmojiTypeBean? = null,
) : Serializable {
    @JsonIgnore
    fun getEmoji(): EmojiType {
        return id?.toInt()?.let { EmojiType.fromValue(it) }!!
    }
}

enum class EmojiTypeBean(val value: Int, val description: String) : Serializable {
    SYSTEM_EMOJI(1, "系统表情"),
    CUSTOM_EMOJI(2, "emoji表情")
}
