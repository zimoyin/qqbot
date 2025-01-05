package io.github.zimoyin.ra3.expand

import io.github.zimoyin.qqbot.bot.message.type.PlainTextMessage

/**
 *
 * @author : zimo
 * @date : 2025/01/05
 */

fun String.toPlainTextMessage(): PlainTextMessage {
    return PlainTextMessage(this)
}