package com.github.zimoyin.qqbot.bot.message.type

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.net.bean.message.MessageAttachment
import java.io.File
import java.io.InputStream
import java.net.URI
import java.util.*

data class ImageMessage(val name: String?, val attachment: MessageAttachment) : MessageItem {
    override fun toStringType(): String {
        return "[Image:${name?.replace("\n", "\\n")}]"
    }

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
        fun create(file: File): ImageMessage {
            return ImageMessage(file.name, MessageAttachment()).apply {
                localFile = file
            }
        }

        /**
         * 构建本地图片信息
         *  注意： 目前仅仅支持频道通过本地发送图片
         */
        @UntestedApi
        fun create(file: InputStream): ImageMessage {
            return ImageMessage(UUID.randomUUID().toString(), MessageAttachment()).apply {
                localFileBytes = file.readBytes()
            }
        }

        /**
         * 构建网络图片信息
         */
        fun create(uri: String): ImageMessage {
            val create = URI.create(uri)
            return ImageMessage(uri, MessageAttachment(uri = "${create.host?:""}${create.path?:""}${create.query?.let { "?$it" }?:""}"))
        }
    }
}
