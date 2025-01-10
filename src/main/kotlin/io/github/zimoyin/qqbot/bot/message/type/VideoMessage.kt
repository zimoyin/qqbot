package io.github.zimoyin.qqbot.bot.message.type

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.net.bean.message.MessageAttachment
import io.github.zimoyin.qqbot.utils.ex.toUrl
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

    @get:JsonIgnore
    val bytes:ByteArray? by lazy {
        localFileBytes?:localFile?.readBytes()?:attachment.getURL()?.toUrl()?.readBytes()
    }

    companion object {
        /**
         * 构建网络视频信息
         */
        @JvmStatic
        fun create(uri: String): VideoMessage {
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
