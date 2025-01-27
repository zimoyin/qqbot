package io.github.zimoyin.qqbot.bot.message.type

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.zimoyin.qqbot.LocalLogger
import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.net.bean.message.MessageAttachment
import io.github.zimoyin.qqbot.utils.ex.toUrl
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream
import java.net.URI
import java.util.*

/**
 * 主动视频消息，只能发不能接收
 */
data class VideoMessage(val name: String?, val attachment: MessageAttachment) : MessageItem {
    override fun toStringType(): String {
        return "[ProactiveVideoMessage:${name?.replace("\n", "\\n")}]"
    }

    @JsonIgnore
    var localFile: File? = null

    @JsonIgnore
    var localFileBytes: ByteArray? = null

    @JsonIgnore
    fun bytes(): ByteArray? {
        return localFileBytes ?: localFile?.readBytes() ?: attachment.getURL()?.toUrl()?.readBytes()
    }

    companion object {
        private val logger = LocalLogger(VideoMessage::class.java)

        /**
         * 构建网络视频信息
         */
        @JvmStatic
        fun create(uri: String): VideoMessage {
            if (File(uri).exists()) throw IllegalArgumentException("Parameter (string) URI cannot be a file")
            val create = URI.create(uri)
            return VideoMessage(
                uri,
                MessageAttachment(
                    protocol = create.scheme ?: "https",
                    uri = "${create.host ?: ""}${create.path ?: ""}${create.query?.let { "?$it" } ?: ""}"
                )
            )
        }

        /**
         * 构建本地视频信息
         * 注意： 目前仅仅支持频道通过本地发送视频
         */
        @JvmStatic
        fun create(file: File): VideoMessage {
            return VideoMessage(file.name, MessageAttachment()).apply {
                if (!file.exists()) throw IllegalArgumentException("Not found file: $file")
                val limit = 8 * 1024 * 1024
                if (file.length() > limit) {
                    logger.warn("文件大小超过${limit}mb，可能会因为网络问题导致发送失败: ${file.length() / 1024 / 1024} mb")
                }
                // 文件格式校验 mp4
                if (!file.extension.endsWith("mp4")) {
                    logger.warn("文件格式不正确，请使用mp4格式的视频文件: ${file.name}")
                }
                localFile = file
            }
        }

        /**
         * 构建本地视频信息
         *  注意： 目前仅仅支持频道通过本地发送视频
         */
        @JvmStatic
        fun create(file: InputStream): VideoMessage {
            return VideoMessage(UUID.randomUUID().toString(), MessageAttachment()).apply {
                localFileBytes = file.readBytes()
            }
        }

        @UntestedApi
        @JvmStatic
        fun create(file: ByteArray): VideoMessage {
            return VideoMessage(UUID.randomUUID().toString(), MessageAttachment()).apply {
                localFileBytes = file
            }
        }
    }
}
