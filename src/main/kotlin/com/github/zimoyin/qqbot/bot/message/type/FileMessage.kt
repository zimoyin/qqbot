package com.github.zimoyin.qqbot.bot.message.type

import com.github.zimoyin.qqbot.net.bean.message.MessageAttachment

data class FileMessage(val name: String?, val attachment: MessageAttachment) : MessageItem {
    override fun toStringType(): String {
        return "[File:${name?.replace("\n", "\\n")}]"
    }
}
