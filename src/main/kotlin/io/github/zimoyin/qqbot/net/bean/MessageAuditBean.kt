package io.github.zimoyin.qqbot.net.bean

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
data class MessageAuditBean(
    /**
     * 消息审核 id
     */
    @field:JsonProperty("audit_id")
    val auditId: String? = null,

    /**
     * 消息 id，只有审核通过事件才会有值
     */
    @field:JsonProperty("message_id")
    val messageId: String? = null,

    /**
     * 频道 id
     */
    @field:JsonProperty("guild_id")
    val guildId: String? = null,

    /**
     * 频道群 id
     */
    @field:JsonProperty("group_openid")
    val groupOpenId: String? = null,

    /**
     * 子频道 id
     */
    @field:JsonProperty("channel_id")
    val channelId: String? = null,

    /**
     * 消息审核时间
     */
    @field:JsonProperty("audit_time")
    val auditTime: String? = null,

    /**
     * 消息创建时间
     */
    @field:JsonProperty("create_time")
    val createTime: String? = null,

    /**
     * 子频道消息 seq，用于消息间的排序
     * seq 在同一子频道中按从先到后的顺序递增，
     * 不同的子频道之间消息无法排序
     */
    @field:JsonProperty("seq_in_channel")
    val seqInChannel: String? = null
): Serializable
