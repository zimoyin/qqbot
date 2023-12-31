package com.github.zimoyin.qqbot.net.bean

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.zimoyin.qqbot.bot.message.EmojiType
import java.io.Serializable
import java.net.URL
import java.time.Instant


/**
 *
 * @author : zimo
 * @date : 2023/12/15
 *
 * 用于向 channel_id 指定的子频道发送消息。
 *
 * 要求操作人在该子频道具有发送消息的权限。
 * 主动消息在频道主或管理设置了情况下，按设置的数量进行限频。在未设置的情况遵循如下限制:
 * 主动推送消息，默认每天往每个子频道可推送的消息数是 20 条，超过会被限制。
 * 主动推送消息在每个频道中，每天可以往 2 个子频道推送消息。超过后会被限制。
 * 不论主动消息还是被动消息，在一个子频道中，每 1s 只能发送 5 条消息。
 * 被动回复消息有效期为 5 分钟。超时会报错。
 * 发送消息接口要求机器人接口需要连接到 websocket 上保持在线状态
 * 有关主动消息审核，可以通过 Intents 中审核事件 MESSAGE_AUDIT 返回 MessageAudited 对象获取结果。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class SendMessageBean(
  /**
     * 选填，消息内容，文本内容，支持内嵌格式
     */
    @field:JsonProperty("content")
    val content: String? = null,

  /**
     * 选填，embed 消息，一种特殊的 ark，详情参考Embed消息
     */
    @field:JsonProperty("embed")
    val embed: MessageEmbed? = null,

  /**
     * 选填，ark 消息,该信息需要申请才能发送，详情参考Ark消息
     * 一种卡片信息
     *  以下默认可使用的 ID 但是发送信息还需要申请：
     * 23 链接+文本列表模板|QQ机器人文档
     * 24 文本+缩略图模板|QQ机器人文档
     * 37 大图模板|QQ机器人文档
     */
    @field:JsonProperty("ark")
//    val ark: JsonObject? = null,
    val ark: MessageArk? = null,

  /**
     * 选填，引用消息
     */
    @field:JsonProperty("message_reference")
    val messageReference: MessageReference? = null,

  /**
     * 选填，图片url地址，平台会转存该图片，用于下发图片消息
     */
    @field:JsonProperty("image")
    val image: String? = null,

  /**
     * 选填，要回复的消息id(Message.id), 在 AT_CREATE_MESSAGE 事件中获取。
     */
    @field:JsonProperty("msg_id")
    val id: String? = null,

  /**
     * 选填，markdown 息
     */
    @field:JsonProperty("markdown")
    val markdown: MessageMarkdown? = null,
)

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

/**
 * Ark消息
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageArk(
  /**
     * Ark模板id（需要先申请）
     */
    @field:JsonProperty("template_id")
    val templateId: Int? = null,

  /**
     * Ark kv值列表
     */
    @field:JsonProperty("kv")
    val kv: List<MessageArkKv>? = null,
) : Serializable

/**
 * Ark kv值
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageArkKv(
  /**
     * Key
     */
    @field:JsonProperty("key")
    val key: String? = null,

  /**
     * Value
     */
    @field:JsonProperty("value")
    val value: String? = null,

  /**
     * Ark obj类型的列表
     */
    @field:JsonProperty("obj")
    val obj: List<MessageArkObj>? = null,
) : Serializable

/**
 * Ark obj类型
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageArkObj(
  /**
     * Ark objkv列表
     */
    @field:JsonProperty("obj_kv")
    val objKv: List<MessageArkObjKv>? = null,
) : Serializable

/**
 * Ark objkv类型
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageArkObjKv(
    /**
     * Key
     */
    @field:JsonProperty("key")
    val key: String? = null,

    /**
     * Value
     */
    @field:JsonProperty("value")
    val value: String? = null,
) : Serializable

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
    val ignoreGetMessageError: Boolean? = null,
) : Serializable

/**
 * Markdown消息
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageMarkdown(
  /**
     * Markdown 模板 id
     */
    @field:JsonProperty("template_id")
    val templateId: Int? = null,

  /**
     * Markdown 模板参数
     */
    @field:JsonProperty("params")
    val params: MessageMarkdownParams? = null,

  /**
     * 原生 Markdown 内容，与 template_id 和 params 参数互斥
     */
    @field:JsonProperty("content")
    val content: String? = null,
) : Serializable

/**
 * Markdown消息参数
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageMarkdownParams(
    /**
     * Markdown 模板 key
     */
    @field:JsonProperty("key")
    val key: String? = null,

    /**
     * Markdown 模板 key 对应的 values
     * 列表长度大小为 1 代表单 value 值，长度大于 1 则为列表类型的参数 values 传参数
     */
    @field:JsonProperty("values")
    val values: List<String>? = null,
) : Serializable


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

/**
 * 频道消息频率设置对象
 * @author : zimo
 * @date : 2023/12/20
 */
data class MessageSetting(
    /**
     * 是否允许创建私信
     */
    @JsonProperty("disable_create_dm")
    val disableCreateDm: String? = null,

    /**
     * 是否允许发送主动消息
     */
    @JsonProperty("disable_push_msg")
    val disablePushMsg: String? = null,

    /**
     * 子频道 ID 数组
     */
    @JsonProperty("channel_ids")
    val channelIds: List<String>? = null,

    /**
     * 每个子频道允许主动推送消息的最大消息条数
     */
    @JsonProperty("channel_push_max_num")
    val channelPushMaxNum: Int? = null,
) : Serializable
