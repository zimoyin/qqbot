package com.github.zimoyin.qqbot.net.bean

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.zimoyin.qqbot.bot.message.EmojiType
import com.github.zimoyin.qqbot.bot.message.type.MarkdownMessage
import com.github.zimoyin.qqbot.utils.JSON
import io.vertx.core.json.JsonObject
import org.intellij.lang.annotations.Language
import java.io.File
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
// TODO 分解文件所有类到 com.github.zimoyin.qqbot.net.bean.message 包中
@JsonIgnoreProperties(ignoreUnknown = true)
data class SendMessageBean(
    /**
     * 选填，消息内容，文本内容，支持内嵌格式
     * 【私聊群聊的必填】
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
     * 注意：私聊与群聊未支持
     */
    @field:JsonProperty("message_reference")
    val messageReference: MessageReference? = null,

    /**
     * 选填，图片url地址，平台会转存该图片，用于下发图片消息
     * 注意：私聊与群聊未支持
     */
    @field:JsonProperty("image")
    val imageURI: String? = null,

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
    val keyboard: String?,

    @JsonIgnore
    val channelFile: File? = null,
    @JsonIgnore
    val channelFileBytes: ByteArray? = null,
    @JsonIgnore
    val videoURI: String? = null,
    @JsonIgnore
    val audioURI: String? = null,


    /////////////////   群聊和私聊的字段   /////////////////

    /**
     * 【必填】
     * 消息类型： 0 文本，2 是 markdown，3 ark 消息，4 embed，7 media 富媒体
     */
    @field:JsonProperty("msg_type")
    var msgType: Int? = null,

    /**
     * 富媒体信息
     * 数据来源"消息收发=>富媒体消息"
     * 示例： {file_info: ""}
     */
    var media: MediaMessageBean? = null,
) {
    /**
     * 推断消息类型
     */
    @JsonIgnore
    fun inferMsgType(): SendMessageBean {
        return this.apply {
            when {
                media != null -> msgType = MSG_TYPE_MEDIA
                imageURI != null -> msgType = MSG_TYPE_MEDIA
                audioURI != null -> msgType = MSG_TYPE_MEDIA
                videoURI != null -> msgType = MSG_TYPE_MEDIA
                content != null -> msgType = MSG_TYPE_TEXT
                markdown != null -> msgType = MSG_TYPE_MARKDOWN
                ark != null -> msgType = MSG_TYPE_ARK
                embed != null -> msgType = MSG_TYPE_EMBED
            }
        }
    }

    /**
     * 将 SendMessageBean 转为 MediaBean
     * 该方法只能构建图片的信息
     */
    @JsonIgnore
    fun toMediaBean(): SendMediaBean {
        require(!(channelFile != null || channelFileBytes != null)) { "ChannelFile and channelFileBytes must be null" }
        require(!(imageURI == null && audioURI == null && videoURI == null)) { "ImageURI, audioURI, and videoURI cannot all be null" }
        return when {
            imageURI != null -> SendMediaBean(
                fileType = SendMediaBean.FILE_TYPE_IMAGE,
                url = imageURI,
            )

            audioURI != null -> SendMediaBean(
                fileType = SendMediaBean.FILE_TYPE_AUDIO,
                url = audioURI,
            )

            else -> SendMediaBean(
                fileType = SendMediaBean.FILE_TYPE_VIDEO,
                url = videoURI,
            )
        }
    }

    @JsonIgnore
    fun toJson(): JsonObject {
        val json = JSON.toJsonObject(this)
        return JsonObject().apply {
            json.forEach {
                if (it.key != null && it.value != null)
                    put(it.key, it.value)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SendMessageBean) return false

        if (content != other.content) return false
        if (embed != other.embed) return false
        if (ark != other.ark) return false
        if (messageReference != other.messageReference) return false
        if (imageURI != other.imageURI) return false
        if (id != other.id) return false
        if (markdown != other.markdown) return false
        if (keyboard != other.keyboard) return false
        if (channelFile != other.channelFile) return false
        if (channelFileBytes != null) {
            if (other.channelFileBytes == null) return false
            if (!channelFileBytes.contentEquals(other.channelFileBytes)) return false
        } else if (other.channelFileBytes != null) return false
        if (msgType != other.msgType) return false
        if (media != other.media) return false
//        if (fileType != other.fileType) return false
//        if (url != other.url) return false
//        if (srv_send_msg != other.srv_send_msg) return false
//        if (file_data != other.file_data) return false

        return true
    }

    override fun hashCode(): Int {
        var result = content?.hashCode() ?: 0
        result = 31 * result + (embed?.hashCode() ?: 0)
        result = 31 * result + (ark?.hashCode() ?: 0)
        result = 31 * result + (messageReference?.hashCode() ?: 0)
        result = 31 * result + (imageURI?.hashCode() ?: 0)
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + (markdown?.hashCode() ?: 0)
        result = 31 * result + (keyboard?.hashCode() ?: 0)
        result = 31 * result + (channelFile?.hashCode() ?: 0)
        result = 31 * result + (channelFileBytes?.contentHashCode() ?: 0)
        result = 31 * result + (msgType ?: 0)
        result = 31 * result + (media?.hashCode() ?: 0)
//        result = 31 * result + (fileType ?: 0)
//        result = 31 * result + (url?.hashCode() ?: 0)
//        result = 31 * result + (srv_send_msg?.hashCode() ?: 0)
//        result = 31 * result + (file_data?.hashCode() ?: 0)
        return result
    }

    fun toStrings(): String {
//        return "{\"content\":  \"$content\", ${if (embed != null) "\"embed\":  $embed, " else ""}${if (ark != null) "\"ark\":  $ark, " else ""}${if (messageReference != null) "\"message_reference\":  $messageReference, " else ""}${if (image != null) "\"image\":  \"$image\", " else ""}${if (id != null) "\"msg_id\":  \"$id\", " else ""}${if (markdown != null) "\"markdown\":  $markdown, " else ""}${if (keyboard != null) "\"keyboard\":  \"$keyboard\", " else ""}${if (channelFile != null) "\"channel_file\":  $channelFile, " else ""}${if (channelFileBytes != null) "\"channel_file_bytes\":  $channelFileBytes, " else ""}${if (msg_type != null) "\"msg_type\":  $msg_type, " else ""}${if (media != null) "\"media\":  $media, " else ""}${if (fileType != null) "\"file_type\":  $fileType, " else ""}${if (url != null) "\"url\":  \"$url\", " else ""}${if (srv_send_msg != null) "\"srv_send_msg\":  $srv_send_msg, " else ""}${if (file_data != null) "\"file_data\":  \"$file_data\" " else ""}}"
        return "{\"content\":  \"$content\", ${if (embed != null) "\"embed\":  $embed, " else ""}${if (ark != null) "\"ark\":  $ark, " else ""}${if (messageReference != null) "\"message_reference\":  $messageReference, " else ""}${if (imageURI != null) "\"image\":  \"$imageURI\", " else ""}${if (id != null) "\"msg_id\":  \"$id\", " else ""}${if (markdown != null) "\"markdown\":  $markdown, " else ""}${if (keyboard != null) "\"keyboard\":  \"$keyboard\", " else ""}${if (channelFile != null) "\"channel_file\":  $channelFile, " else ""}${if (channelFileBytes != null) "\"channel_file_bytes\":  $channelFileBytes, " else ""}${if (msgType != null) "\"msg_type\":  $msgType, " else ""}${if (media != null) "\"media\":  $media, " else ""}]}"
    }


    companion object {
        const val MSG_TYPE_TEXT = 0
        const val MSG_TYPE_MARKDOWN = 2
        const val MSG_TYPE_ARK = 3
        const val MSG_TYPE_EMBED = 4
        const val MSG_TYPE_MEDIA = 7
    }
}

/**
 * 富文本内容
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class SendMediaBean(
    /**
     * 媒体类型：1 图片，2 视频，3 语音，4 文件（暂不开放）
     * 资源格式要求
     * 图片：png/jpg，视频：mp4，语音：silk
     */
    @field:JsonProperty("file_type")
    val fileType: Int? = null,

    /**
     * 需要发送媒体资源的url
     */
    val url: String? = null,

    /**
     * 设置 true 会直接发送消息到目标端，且会占用主动消息频次
     */
    val srv_send_msg: Boolean? = false,

    /**
     * 【暂未支持】
     */
    val file_data: String? = null,
) {
    companion object {
        const val FILE_TYPE_IMAGE = 1
        const val FILE_TYPE_VIDEO = 2
        const val FILE_TYPE_AUDIO = 3
    }
}

/**
 * SendMediaBean 请求后返回的 Bean
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MediaMessageBean(
    /**
     * 文件 ID
     */
    @JsonProperty("file_uuid")
    val fileUUID: String? = null,

    /**
     * 文件信息，用于发消息接口的 media 字段使用
     */
    @JsonProperty("file_info")
    val fileInfo: String? = null,

    /**
     * 有效期，表示剩余多少秒到期，到期后 file_info 失效，当等于 0 时，表示可长期使用
     */
    @JsonProperty("ttl")
    val ttl: Int? = null,

    /**
     * 发送消息的唯一ID，当srv_send_msg设置为true时返回
     */
    @JsonProperty("id")
    val id: String? = null,
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
     * 如果你确信你有权限不需要申请模板可以为 null
     */
//    @field:JsonProperty("template_id")
    @field:JsonProperty("custom_template_id")
    val templateId: String? = null,

    /**
     * Markdown 模板参数
     */
    @field:JsonProperty("params")
    val params: List<MessageMarkdownParam>? = null,

    /**
     * 原生 Markdown 内容，与 template_id 和 params 参数互斥
     */
    @field:JsonProperty("content")
    val content: String? = null,
) : Serializable {
    @JsonIgnore
    fun toMessage(): MarkdownMessage {
        return MarkdownMessage(this)
    }

    @JsonIgnore
    fun toJson(): String {
        return JSON.toJsonString(this)
    }

    companion object {

        @JsonIgnore
        fun create(@Language("JSON") json: String): MessageMarkdown {
            return JSON.toObject<MessageMarkdown>(json)
        }
    }
}

/**
 * Markdown消息参数
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageMarkdownParam(
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
) : Serializable {

    @JsonIgnore
    fun add(value: MessageMarkdownParam): ArrayList<MessageMarkdownParam> = arrayListOf(this).apply {
        add(value)
    }

    companion object {
        @JsonIgnore
        fun create(key: String, value: Any): MessageMarkdownParam {
            return MessageMarkdownParam(key, listOf(value.toString()))
        }

        @JsonIgnore
        fun create(@Language("JSON") json: String): MessageMarkdownParam {
            return JSON.toObject<MessageMarkdownParam>(json)
        }
    }
}


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
