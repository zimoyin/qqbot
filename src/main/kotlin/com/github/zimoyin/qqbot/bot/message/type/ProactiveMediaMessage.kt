package com.github.zimoyin.qqbot.bot.message.type

import com.github.zimoyin.qqbot.net.bean.MediaMessageBean
import com.github.zimoyin.qqbot.net.bean.SendMediaBean

/**
 * 主动发送资源消息，只能发不能收
 */
data class ProactiveMediaMessage(val media: MediaMessageBean) : MessageItem {
    override fun toStringType(): String {
        return "[ProactiveMediaMessage:${media}]"
    }

    companion object {
        fun create(media: MediaMessageBean): ProactiveMediaMessage {
            return ProactiveMediaMessage(media)
        }
    }
}
