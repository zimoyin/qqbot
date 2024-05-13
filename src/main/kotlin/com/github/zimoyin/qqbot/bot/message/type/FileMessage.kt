package com.github.zimoyin.qqbot.bot.message.type

import com.github.zimoyin.qqbot.net.bean.message.MessageAttachment

/**
 * 文件消息
 * 用处不大
 */
data class FileMessage(val name: String?, val attachment: MessageAttachment) : MessageItem {
    override fun toStringType(): String {
        return "[File:${name?.replace("\n", "\\n")}]"
    }
}
