package io.github.zimoyin.qqbot.bot.message.type

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.net.bean.message.MessageAttachment
import io.github.zimoyin.qqbot.utils.ex.toUrl
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream
import java.net.URI
import java.util.*

/**
 * 主动音频消息，只能发不能收
 */
data class AudioMessage(val name: String?, val attachment: MessageAttachment) : MessageItem {
    override fun toStringType(): String {
        return "[ProactiveAudioMessage:${name?.replace("\n", "\\n")}]"
    }

    @JsonIgnore
    var localFile: File? = null

    @JsonIgnore
    var localFileBytes: ByteArray? = null

    @JsonIgnore
    fun bytes(): ByteArray? {
        return localFileBytes?:localFile?.readBytes()?:attachment.getURL()?.toUrl()?.readBytes()
    }

    companion object {
        /**
         * 构建网络视频信息
         */
        @JvmStatic
        fun create(uri: String): AudioMessage {
            if (File(uri).exists()) throw IllegalArgumentException("Parameter (string) URI cannot be a file")
            val create = URI.create(uri)
            return AudioMessage(uri, MessageAttachment( protocol = create.scheme ?: "https",uri = "${create.host?:""}${create.path?:""}${create.query?.let { "?$it" }?:""}"))
        }

        /**
         * 构建本地图片信息
         * 注意： 目前仅仅支持频道通过本地发送图片
         */
        @UntestedApi
        @JvmStatic
        fun create(file: File): AudioMessage {
            return AudioMessage(file.name, MessageAttachment()).apply {
                if (!file.exists()) throw IllegalArgumentException("Not found file: $file")
                val limit = 8 * 1024 * 1024
                if (file.length() > limit) {
                    LoggerFactory.getLogger(ImageMessage::class.java).warn("文件大小超过${limit}mb，可能会因为网络问题导致发送失败: ${file.length() / 1024 / 1024} mb")
                }
                localFile = file
            }
        }

        /**
         * 构建本地图片信息
         *  注意： 目前仅仅支持频道通过本地发送图片
         */
        @UntestedApi
        @JvmStatic
        fun create(file: InputStream): AudioMessage {
            return AudioMessage(UUID.randomUUID().toString(), MessageAttachment()).apply {
                localFileBytes = file.readBytes()
            }
        }

        @JvmStatic
        fun create(file: ByteArray): AudioMessage {
            return AudioMessage(UUID.randomUUID().toString(), MessageAttachment()).apply {
                localFileBytes = file
            }
        }


    }
}
