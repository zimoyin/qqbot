package io.github.zimoyin.qqbot.net.bean.message.send

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.zimoyin.qqbot.utils.ex.md5
import java.io.Serializable

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
     *  Base64
     */
    val file_data: String? = null,
) {
    companion object {
        const val FILE_TYPE_IMAGE = 1
        const val FILE_TYPE_VIDEO = 2
        const val FILE_TYPE_AUDIO = 3
    }

    override fun toString(): String {
        return "SendMediaBean(fileType=$fileType, url=$url, srv_send_msg=$srv_send_msg, file_data=${if (file_data==null) "null" else "not null"})"
    }

    fun getFileDataMd5(): String {
        return url?.md5() ?: file_data?.md5()?:throw RuntimeException("file_data and url is null")
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
):Serializable {

    /**
     * 创建时间戳,单位是 S
     */
    @JsonIgnore
    val createTimespan: Long = System.currentTimeMillis() / 1000


    /**
     * 是否过期
     */
    fun isExpired(): Boolean {
        return if (ttl == 0) false else System.currentTimeMillis() / 1000 - createTimespan > (ttl ?: -1)
    }
}
