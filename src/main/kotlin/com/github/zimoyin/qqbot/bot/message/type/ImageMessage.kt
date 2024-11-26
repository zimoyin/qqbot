package com.github.zimoyin.qqbot.bot.message.type

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.net.bean.message.MessageAttachment
import com.github.zimoyin.qqbot.net.bean.message.MessageReference
import com.github.zimoyin.qqbot.net.bean.message.send.SendMediaBean
import com.github.zimoyin.qqbot.net.bean.message.send.SendMessageBean
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream
import java.net.URI
import java.util.*

data class ImageMessage(val name: String?, val attachment: MessageAttachment) : MessageItem {
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
    var localFile: File? = null

    @JsonIgnore
    var localFileBytes: ByteArray? = null

    companion object {
        /**
         * 构建本地图片信息
         * 注意： 目前仅仅支持频道通过本地发送图片
         */
        @UntestedApi
        @JvmStatic
        fun create(file: File): ImageMessage {
            return ImageMessage(file.name, MessageAttachment()).apply {
                if (!file.exists()) throw IllegalArgumentException("Not found file: $file")
                if (file.length() > 4 * 1024 * 1024) {
                    LoggerFactory.getLogger(ImageMessage::class.java).warn("图片大小超过4mb，可能会导致发送失败")
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

        /**
         * 构建网络图片信息
         */
        @JvmStatic
        fun create(uri: String): ImageMessage {
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
