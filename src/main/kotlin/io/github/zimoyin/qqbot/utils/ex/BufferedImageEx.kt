package io.github.zimoyin.qqbot.utils.ex

import io.github.zimoyin.qqbot.bot.message.type.ImageMessage
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

/**
 *
 * @author : zimo
 * @date : 2025/01/15
 */
fun BufferedImage.bytes(type:String = "png"): ByteArray {
    val inputStream = ByteArrayOutputStream()
    ImageIO.write(this, "png", inputStream)
    return inputStream.toByteArray()
}

fun BufferedImage.toMessageImage(type:String = "png"): ImageMessage {
    return ImageMessage.create(this.bytes(type))
}
