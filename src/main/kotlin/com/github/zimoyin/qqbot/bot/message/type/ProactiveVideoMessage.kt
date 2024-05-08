package com.github.zimoyin.qqbot.bot.message.type

import com.github.zimoyin.qqbot.net.bean.MessageAttachment
import java.net.URI

/**
 * 主动视频消息，只能发不能接收
 */
data class ProactiveVideoMessage(val name: String?, val attachment: MessageAttachment) : MessageItem {
    override fun toStringType(): String {
        return "[ProactiveVideoMessage:${name?.replace("\n", "\\n")}]"
    }

    companion object {
        /**
         * 构建网络视频信息
         */
        fun create(uri: String): ProactiveVideoMessage {
            val create = URI.create(uri)
            return ProactiveVideoMessage(uri, MessageAttachment(uri = "${create.host?:""}${create.path?:""}${create.query?.let { "?$it" }?:""}"))
        }
    }
}
