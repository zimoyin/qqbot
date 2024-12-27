package io.github.zimoyin.qqbot.net.bean.message

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 表示一个平台事件的数据类，用于解析从平台接收到的事件信息。
 */
data class InteractionEventData(
    /**
     * 平台方事件 ID，可以用于被动消息发送。
     */
    @field:JsonProperty("id") val id: String,

    /**
     * 消息类型：
     * - 11 表示消息按钮，
     * - 12 表示单聊快捷菜单。
     */
    @field:JsonProperty("type") val eventType: Int? = null,

    /**
     * 事件发生的场景：
     * - "c2c" 表示用户到用户的单聊场景，
     * - "group" 表示群聊场景，
     * - "guild" 表示频道场景。
     */
    @field:JsonProperty("scene") val scene: String? = null,

    /**
     * 聊天类型：
     * - 0 表示频道场景，
     * - 1 表示群聊场景，
     * - 2 表示单聊场景。
     */
    @field:JsonProperty("chat_type") val chatType: Int? = null,

    /**
     * 触发时间，遵循 RFC 3339 格式。
     */
    @field:JsonProperty("timestamp") val timestamp: String? = null,

    /**
     * 频道的 openid，仅在频道场景提供该字段。
     */
    @field:JsonProperty("guild_id") val guildId: String? = null,

    /**
     * 文字子频道的 openid，仅在频道场景提供该字段。
     */
    @field:JsonProperty("channel_id") val channelId: String? = null,

    /**
     * 单聊按钮触发的用户 openid，仅在单聊场景提供该字段。
     */
    @field:JsonProperty("user_openid") val userOpenid: String? = null,

    /**
     * 群的 openid，仅在群聊场景提供该字段。
     */
    @field:JsonProperty("group_openid") val groupOpenid: String? = null,

    /**
     * 按钮触发用户的群成员 openid，仅在群聊场景提供该字段。
     */
    @field:JsonProperty("group_member_openid") val groupMemberOpenid: String? = null,

    /**
     * 包含操作按钮相关数据的嵌套对象。
     */
    @field:JsonProperty("data") val dataResolved: DataResolved? = null,

    /**
     * 版本号，默认为 1。
     */
    @field:JsonProperty("version") val version: Int = 1
)

/**
 * 表示 [InteractionEventData] 中 'data.resolved' 字段的嵌套数据类，用于解析与操作按钮相关的额外信息。
 */
data class DataResolved(
    /**
     * 操作按钮的 data 字段值（在发送消息按钮时设置）。
     */
    @field:JsonProperty("button_data") val buttonData: String? = null,

    /**
     * 操作按钮的 id 字段值（在发送消息按钮时设置）。
     */
    @field:JsonProperty("button_id") val buttonId: String? = null,

    /**
     * 操作的用户 userid，仅在频道场景提供该字段。
     */
    @field:JsonProperty("user_id") val userId: String? = null,

    /**
     * 操作按钮的 id 字段值，仅自定义菜单提供该字段（在管理端设置）。
     */
    @field:JsonProperty("feature_id") val featureId: String? = null,

    /**
     * 操作的消息 id，目前仅在频道场景提供该字段。
     */
    @field:JsonProperty("message_id") val messageId: String? = null
)
