package io.github.zimoyin.ra3.expand

import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.message.type.ImageMessage
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 *
 * @author : zimo
 * @date : 2025/01/05
 */

@OptIn(UntestedApi::class)
fun File.toImageMessage(): ImageMessage {
    return ImageMessage.create(this)
}

fun File.toBufferedImage(): BufferedImage {
    return ImageIO.read(this)
}