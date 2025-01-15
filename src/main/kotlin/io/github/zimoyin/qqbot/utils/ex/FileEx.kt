package io.github.zimoyin.qqbot.utils.ex

import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.message.type.ImageMessage
import java.io.File

/**
 *
 * @author : zimo
 * @date : 2025/01/15
 */
@OptIn(UntestedApi::class)
fun File.toImageMessage(): ImageMessage {
    return ImageMessage.create(this)
}
