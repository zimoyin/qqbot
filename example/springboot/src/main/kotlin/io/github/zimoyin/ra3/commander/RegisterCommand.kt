package io.github.zimoyin.ra3.commander

import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.message.type.ImageMessage
import io.github.zimoyin.qqbot.bot.message.type.PlainTextMessage
import io.github.zimoyin.ra3.annotations.Commander
import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import io.github.zimoyin.ra3.config.ResourcesReleaseConfig
import io.github.zimoyin.ra3.expand.bytes
import io.github.zimoyin.ra3.service.CommandParser
import io.github.zimoyin.ra3.service.IRegisterService
import org.slf4j.LoggerFactory
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 *
 * @author : zimo
 * @date : 2025/01/03
 */
@Commander("/注册", executeMethod = "register")
class RegisterCommand(
    private val config: ResourcesReleaseConfig,
    val parser: CommandParser,
    val service: IRegisterService
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    val image1 = File(config.targetPath, "camp_1.png")
    val image2 = File(config.targetPath, "camp_2.png")
    val image3 = File(config.targetPath, "camp_3.png")

    /**
     * 注册命令
     */
    @OptIn(UntestedApi::class)
    fun register(event: MessageEvent) {
        val uid = event.sender.id
        val nick = event.sender.nick
        val parse = parser.parse(event)
        val param = parse.params.lastOrNull()
        logger.debug("注册命令：{}", parse)
        if (param == null){
            event.reply(
                ImageMessage.create(combineImagesVertically(image1, image2, image3).bytes()),
                PlainTextMessage("请选择你的阵营（1/2/3）\n请回复：@机器人 /注册 1/2/3")
            )
        }else{
            kotlin.runCatching {
                val image = when (param.toInt()) {
                    1 -> image1
                    2 -> image2
                    3 -> image3
                    else -> throw IllegalArgumentException("参数错误")
                }
                if (service.register(uid, nick, param.toInt())) {
                    event.reply(ImageMessage.create(image), PlainTextMessage("欢迎回来，${nick} 指挥官!"))
                }else{
                    event.reply(ImageMessage.create(image), PlainTextMessage("欢迎回来，${nick} 指挥官!\n您无需再次注册"))
                }
            }.onFailure {
                event.reply("注册失败，请重试")
                logger.error("注册失败", it)
            }
        }
    }


    fun combineImagesVertically(file1: File, file2: File, file3: File): BufferedImage {
        // 加载图像文件为 BufferedImage 对象。
        val image1 = ImageIO.read(file1)
        val image2 = ImageIO.read(file2)
        val image3 = ImageIO.read(file3)

        // 获取每个图像的宽度，并假设所有图像的宽度相同或根据需要调整。
        val width = maxOf(image1.width, image2.width, image3.width)
        // 计算总高度。
        val totalHeight = image1.height + image2.height + image3.height

        // 创建一个新的 BufferedImage，其大小足以容纳三个图像。
        val combinedImage = BufferedImage(width, totalHeight, BufferedImage.TYPE_INT_ARGB)
        // 使用 Graphics2D 绘制位图。
        val g2d: Graphics2D = combinedImage.createGraphics()
        // 将第一个图像绘制到画布上。
        g2d.drawImage(image1, 0, 0, null)
        // 将第二个图像绘制到第一个图像下方。
        g2d.drawImage(image2, 0, image1.height, null)
        // 将第三个图像绘制到第二个图像下方。
        g2d.drawImage(image3, 0, image1.height + image2.height, null)
        // 释放图形上下文。
        g2d.dispose()

        return combinedImage
    }
}