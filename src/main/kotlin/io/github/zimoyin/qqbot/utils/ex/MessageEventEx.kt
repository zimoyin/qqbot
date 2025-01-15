package io.github.zimoyin.qqbot.utils.ex

import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.message.type.*
import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.net.URI
import java.net.URL
import javax.imageio.ImageIO

/**
 *
 * @author : zimo
 * @date : 2025/01/15
 */
@OptIn(UntestedApi::class)
fun MessageEvent.reply(vararg args: Any?) {
    val items = args.filterNotNull().map {
        when (it) {
            is MessageItem -> it
            is String, is Number, is Boolean, is Char -> PlainTextMessage(it.toString())
            is File -> if (it.extension.contains("png") || it.extension.contains("jpg") || it.extension.contains("jpeg")) {
                it.toImageMessage()
            } else if (it.extension.contains("mp4") || it.extension.contains("avi")) {
                VideoMessage.create(it)
            } else {
                AudioMessage.create(it)
            }

            is BufferedImage -> it.toMessageImage()
            is InputStream -> ImageMessage.create(it.readAllBytes())
            is ByteArray -> ImageMessage.create(it)
            is URL, is URI -> ImageMessage.create(it.toString())
            else -> throw IllegalArgumentException("not support type ${it.javaClass.name}")
        }
    }
    reply(*items.toTypedArray())
}
