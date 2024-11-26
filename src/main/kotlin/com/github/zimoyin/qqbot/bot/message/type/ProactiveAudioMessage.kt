package com.github.zimoyin.qqbot.bot.message.type

import com.github.zimoyin.qqbot.net.bean.message.MessageAttachment
import java.net.URI

/**
 * 主动音频消息，只能发不能收
 */
data class ProactiveAudioMessage(val name: String?, val attachment: MessageAttachment) : MessageItem {
    override fun toStringType(): String {
        return "[ProactiveAudioMessage:${name?.replace("\n", "\\n")}]"
    }

    companion object {
        /**
         * 构建网络视频信息
         */
        @JvmStatic
        fun create(uri: String): ProactiveAudioMessage {
            val create = URI.create(uri)
            return ProactiveAudioMessage(uri, MessageAttachment( protocol = create.scheme ?: "https",uri = "${create.host?:""}${create.path?:""}${create.query?.let { "?$it" }?:""}"))
        }
    }
}
