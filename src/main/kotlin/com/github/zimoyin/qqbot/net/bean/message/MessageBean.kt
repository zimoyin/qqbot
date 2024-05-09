package com.github.zimoyin.qqbot.net.bean.message

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.zimoyin.qqbot.net.bean.*
import java.io.Serializable
import java.time.Instant

/**
 * 消息对象
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Message(
    /**
     * 消息 id
     */
    @field:JsonProperty("id")
    val msgID: String? = null,

    /**
     * 频道 id
     */
    @field:JsonProperty("guild_id")
    val guildID: String? = null,

    /**
     * 子频道 id
     */
    @field:JsonProperty("channel_id")
    val channelID: String? = null,


    /**
     * 群聊 id
     */
    @field:JsonProperty("group_openid")
    val groupID: String? = null,

    /**
     * 消息内容
     */
    @field:JsonProperty("content")
    val content: String? = null,

    /**
     * 消息创建时间
     */
    @field:JsonProperty("timestamp")
    val timestamp: Instant? = null,

    /**
     * 消息编辑时间
     */
    @field:JsonProperty("edited_timestamp")
    val editedTimestamp: Instant? = null,

    /**
     * 是否是@全员消息
     */
    @field:JsonProperty("mention_everyone")
    val mentionEveryone: Boolean? = null,

    /**
     * 消息创建者
     */
    @field:JsonProperty("author")
    val author: User? = null,

    /**
     * 附件
     */
    @field:JsonProperty("attachments")
    val attachments: List<MessageAttachment>? = null,

    /**
     * embed 卡片信息
     */
    @field:JsonProperty("embeds")
    val embeds: List<MessageEmbed>? = null,

    /**
     * 消息中@的人
     */
    @field:JsonProperty("mentions")
    val mentions: List<User>? = null,

    /**
     * 消息创建者的member信息
     */
    @field:JsonProperty("member")
    val member: MemberBean? = null,

    /**
     * ark消息
     */
    @field:JsonProperty("ark")
    val ark: MessageArk? = null,

    /**
     * 用于消息间的排序
     * seq 在同一子频道中按从先到后的顺序递增
     * 不同的子频道之间消息无法排序
     * (目前只在消息事件中有值，2022年8月1日 后续废弃)
     */
    @field:JsonProperty("seq")
    @Deprecated("自2022年8月1日起废除 @see seqInChannel")
    val seq: Int? = null,

    /**
     * 子频道消息 seq
     * 用于消息间的排序
     * seq 在同一子频道中按从先到后的顺序递增
     * 不同的子频道之间消息无法排序
     */
    @field:JsonProperty("seq_in_channel")
    val seqInChannel: String? = null,

    /**
     * 引用消息对象
     */
    @field:JsonProperty("message_reference")
    val messageReference: MessageReference? = null,

    @field:JsonProperty("direct_message")
    val directMessage: Boolean? = null,


    /**
     * src_guild_id 用于私信场景下识别真实的来源频道id（即用户发起私信的频道id)。
     * guild_id 为私信场景下的临时频道id，并非真实频道id，因此不应用作其他地方。
     *
     * 该ID用于判断该用户来源的子频道，如果发生信息到私聊会话请不要使用，他会发送到子频道中
     */
    @field:JsonProperty("src_guild_id")
    val srcGuildID: String? = null,
) : Serializable

