package com.github.zimoyin.qqbot.bot.message.type

import java.io.Serializable

interface MessageItem : Serializable {
    /**
     * 获取消息内容
     */
    fun toContent(): String {
        return ""
    }

    /**
     * 信息类型与部分信息
     */
    fun toStringType(): String {
        return toString()
    }

    /**
     * 构建消息元信息
     */
    fun toMetaContent(): String {
        return ""
    }
}
