package com.github.zimoyin.qqbot.net.bean.message.send

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.zimoyin.qqbot.bot.message.type.KeyboardMessage
import com.github.zimoyin.qqbot.net.bean.message.MessageArk
import com.github.zimoyin.qqbot.net.bean.message.MessageEmbed
import com.github.zimoyin.qqbot.net.bean.message.MessageMarkdown
import com.github.zimoyin.qqbot.net.bean.message.MessageReference
import com.github.zimoyin.qqbot.utils.JSON
import com.github.zimoyin.qqbot.utils.ex.toBase64
import io.vertx.core.json.JsonObject
import java.io.File

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
    val fileUri: String? = null,

    /**
     * 选填，要回复的消息id(Message.id), 在 AT_CREATE_MESSAGE 事件中获取。
     */
    @field:JsonProperty("msg_id")
    val id: String? = null,

    /**
     * 选填，前置收到的事件 ID，用于发送被动消息
     * 支持事件：
     * 1. "INTERACTION_CREATE"(未实现)、"GROUP_ADD_ROBOT"、"GROUP_MSG_RECEIVE"(未实现)
     * 2. "C2C_MSG_RECEIVE"(未实现)、"FRIEND_ADD"(未实现)
     * 3. 其他事件支持未知
     */
    @field:JsonProperty("event_id")
    val eventID: String? = null,

    /**
     * 选填，markdown 息
     */
    @field:JsonProperty("markdown")
    val markdown: MessageMarkdown? = null,
    val keyboard: KeyboardMessage?,

    @JsonIgnore
    val file: File? = null,
    @JsonIgnore
    val fileType: Int = SendMediaBean.FILE_TYPE_IMAGE,
    @JsonIgnore
    val fileBytes: ByteArray? = null,

//    @JsonIgnore
//    val fileUri: String? = null,


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
                fileUri != null -> msgType = MSG_TYPE_MEDIA
                file != null -> msgType = MSG_TYPE_MEDIA
                fileBytes != null -> msgType = MSG_TYPE_MEDIA
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
        require(!(file != null && fileBytes != null && fileUri != null)) {
            "file or url is null"
        }
        val isSrvSendMsg = id == null


        return when {

            fileUri != null -> SendMediaBean(
                fileType = fileType,
                url = fileUri,
                srv_send_msg = isSrvSendMsg
            )

            fileBytes != null -> SendMediaBean(
                fileType = fileType,
                file_data = fileBytes.toBase64(),
                srv_send_msg = isSrvSendMsg
            )

            file != null -> SendMediaBean(
                fileType = fileType,
                file_data = file.readBytes().toBase64(),
                srv_send_msg = isSrvSendMsg
            )

            else -> throw IllegalArgumentException("file or url is null")
        }
    }

    @JsonIgnore
    fun toJson(): JsonObject {
        val json = JSON.toJsonObject(this)
        if (keyboard != null) json.put("keyboard", JSON.toJsonObject(keyboard.keyboard))
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
        if (fileUri != other.fileUri) return false
        if (id != other.id) return false
        if (markdown != other.markdown) return false
        if (keyboard != other.keyboard) return false
        if (file != other.file) return false
        if (fileBytes != null) {
            if (other.fileBytes == null) return false
            if (!fileBytes.contentEquals(other.fileBytes)) return false
        } else if (other.fileBytes != null) return false
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
        result = 31 * result + (fileUri?.hashCode() ?: 0)
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + (markdown?.hashCode() ?: 0)
        result = 31 * result + (keyboard?.hashCode() ?: 0)
        result = 31 * result + (file?.hashCode() ?: 0)
        result = 31 * result + (fileBytes?.contentHashCode() ?: 0)
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
        return "{\"content\":  \"$content\", ${if (embed != null) "\"embed\":  $embed, " else ""}${if (ark != null) "\"ark\":  $ark, " else ""}${if (messageReference != null) "\"message_reference\":  $messageReference, " else ""}${if (fileUri != null) "\"image\":  \"$fileUri\", " else ""}${if (id != null) "\"msg_id\":  \"$id\", " else ""}${if (markdown != null) "\"markdown\":  $markdown, " else ""}${if (keyboard != null) "\"keyboard\":  \"$keyboard\", " else ""}${if (file != null) "\"channel_file\":  $file, " else ""}${if (fileBytes != null) "\"channel_file_bytes\":  $fileBytes, " else ""}${if (msgType != null) "\"msg_type\":  $msgType, " else ""}${if (media != null) "\"media\":  $media, " else ""}]}"
    }


    companion object {
        const val MSG_TYPE_TEXT = 0
        const val MSG_TYPE_MARKDOWN = 2
        const val MSG_TYPE_ARK = 3
        const val MSG_TYPE_EMBED = 4
        const val MSG_TYPE_MEDIA = 7
    }
}
