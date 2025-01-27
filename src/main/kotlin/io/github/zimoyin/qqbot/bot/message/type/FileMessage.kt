package io.github.zimoyin.qqbot.bot.message.type

import io.github.zimoyin.qqbot.net.bean.message.MessageAttachment

/**
 * 文件消息
 * 用处不大
 */
open class FileMessage(open val name: String?, open val attachment: MessageAttachment) : MessageItem {
    override fun toStringType(): String {
        return "[File:${name?.replace("\n", "\\n")}]"
    }
}
