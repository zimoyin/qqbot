package io.github.zimoyin.ra3.expand

import io.github.zimoyin.qqbot.bot.message.type.ImageMessage
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import javax.imageio.ImageIO

/**
 *
 * @author : zimo
 * @date : 2025/01/04
 */
fun BufferedImage.bytes(type:String = "png"): ByteArray {
    val inputStream = ByteArrayOutputStream()
    ImageIO.write(this, "png", inputStream)
    return inputStream.toByteArray()
}

fun BufferedImage.toMessageImage(type:String = "png"): ImageMessage {
    return ImageMessage.create(this.bytes(type))
}