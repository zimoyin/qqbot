package io.github.zimoyin.qqbot.bot.message.type

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.zimoyin.qqbot.LocalLogger
import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.net.bean.message.MessageAttachment
import io.github.zimoyin.qqbot.net.bean.message.send.SendMediaBean
import io.github.zimoyin.qqbot.utils.ex.toUrl
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream
import java.net.URI
import java.util.*

class ImageMessage(override val name: String?, override val attachment: MessageAttachment) : FileMessage(name, attachment) {
    override fun toStringType(): String {
        return "[Image:${name?.replace("\n", "\\n")}]"
    }

    /**
     * 将图片信息转换为发送图片的Bean
     */
    @Deprecated("未使用")
    fun convertToSendMediaBean() = SendMediaBean(
        fileType = SendMediaBean.FILE_TYPE_IMAGE,
        url = this.attachment.getURL(),
    )


    @JsonIgnore
    @get:JvmSynthetic
    @set:JvmSynthetic
    var localFile: File? = null

    @JsonIgnore
    @get:JvmSynthetic
    @set:JvmSynthetic
    var localFileBytes: ByteArray? = null

    @JsonIgnore
    fun bytes(): ByteArray? {
        return localFileBytes ?: localFile?.readBytes() ?: attachment.getURL()?.toUrl()?.readBytes()
    }

    companion object {
        private val logger = LocalLogger(ImageMessage::class.java)

        /**
         * 构建本地图片信息
         * 注意： 目前仅仅支持频道通过本地发送图片
         */
        @UntestedApi
        @JvmStatic
        fun create(file: File): ImageMessage {
            return ImageMessage(file.name, MessageAttachment()).apply {
                if (!file.exists()) throw IllegalArgumentException("Not found file: $file")

                val limit = 8 * 1024 * 1024
                if (file.length() > limit) {
                    logger.warn("文件大小超过${limit}mb，可能会因为网络问题导致发送失败: ${file.length() / 1024 / 1024} mb")
                }
                // 文件格式校验 jpg/png/jepg
                if (!file.extension.endsWith("jpg") && !file.extension.endsWith("png") && !file.extension.endsWith("jpeg")) {
                    logger.warn("文件格式不正确，仅支持jpg/png/jpeg格式")
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
        fun create(file: InputStream): ImageMessage {
            return ImageMessage(UUID.randomUUID().toString(), MessageAttachment()).apply {
                localFileBytes = file.readBytes()
            }
        }

        @JvmStatic
        fun create(file: ByteArray): ImageMessage {
            return ImageMessage(UUID.randomUUID().toString(), MessageAttachment()).apply {
                localFileBytes = file
            }
        }

        /**
         * 构建网络图片信息
         */
        @JvmStatic
        fun create(uri: String): ImageMessage {
            if (File(uri).exists()) throw IllegalArgumentException("Parameter (string) URI cannot be a file")
            val create = URI.create(uri)
            return ImageMessage(
                uri,
                MessageAttachment(
                    protocol = create.scheme ?: "https",
                    uri = "${create.host ?: ""}${create.path ?: ""}${create.query?.let { "?$it" } ?: ""}"
                )
            )
        }
    }
}
