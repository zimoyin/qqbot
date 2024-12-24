package io.github.zimoyin.qqbot.bot.message.type

import io.github.zimoyin.qqbot.net.bean.message.send.MediaMessageBean


/**
 * 主动发送资源消息，只能发不能收
 */
data class ProactiveMediaMessage(val media: MediaMessageBean) : MessageItem {
    override fun toStringType(): String {
        return "[ProactiveMediaMessage:${media}]"
    }

    companion object {
        @JvmStatic
        fun create(media: MediaMessageBean): ProactiveMediaMessage {
            return ProactiveMediaMessage(media)
        }
    }
}
