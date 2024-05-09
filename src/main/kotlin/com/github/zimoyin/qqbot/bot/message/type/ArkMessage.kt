package com.github.zimoyin.qqbot.bot.message.type

import com.github.zimoyin.qqbot.net.bean.message.MessageArk
import com.github.zimoyin.qqbot.utils.JSON

data class ArkMessage(val ark: MessageArk, val content: String = JSON.toJsonString(ark)) : MessageItem {
    override fun toContent(): String {
        return content
    }

    override fun toStringType(): String {
        return "[MessageArk:${ark.templateId}]"
    }

    override fun toMetaContent(): String {
        return content
    }
}
