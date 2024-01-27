package com.github.zimoyin.qqbot.bot.message.type

import org.intellij.lang.annotations.Language

/**
 *
 * @author : zimo
 * @date : 2024/01/27
 *
 * TODO 后续支持 keyboard 对象
 */
data class KeyboardMessage(
    @Language("json") val keyboard: String,
) : MessageItem {

    fun toKeyboard(): Unit {
        TODO("后续支持将 json 转为 keyboard 对象")
    }
}
