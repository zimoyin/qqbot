package io.github.zimoyin.ra3.commander

import io.github.zimoyin.qqbot.bot.message.type.ImageMessage
import io.github.zimoyin.qqbot.bot.message.type.MessageItem
import io.github.zimoyin.qqbot.bot.message.type.PlainTextMessage
import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import io.github.zimoyin.ra3.annotations.Commander
import io.github.zimoyin.ra3.config.ResourcesReleaseConfig
import io.github.zimoyin.ra3.expand.*
import io.github.zimoyin.ra3.service.CommandParser
import io.github.zimoyin.ra3.utils.ImageUtil
import io.github.zimoyin.ra3.utils.ImageUtil.loadImage
import io.github.zimoyin.ra3.utils.ImageUtilEx.Companion.addTextToAutoSizeImage
import io.github.zimoyin.ra3.utils.ImageUtilEx.Companion.combineImagesVertically
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.awt.image.BufferedImage
import java.io.File

/**
 *
 * @author : zimo
 * @date : 2025/01/05
 */
@Component
class TouristTramcar : AbsCommander<MessageEvent>() {
    val entityImage by lazy { File(config.targetPath, ResourcesReleaseConfig.CAMP_2_ENTITY_TRAMCAR_1_IMAGE) }
    val bgi by lazy { File(config.targetPath, ResourcesReleaseConfig.COMMAND_BACKGROUND_IMAGE) }
    val divider by lazy { File(config.targetPath, ResourcesReleaseConfig.DIVIDER_IMAGE) }
    val image1 by lazy { File(config.targetPath, ResourcesReleaseConfig.TOURIST_TRAMCAR_IMAGE) }

    override fun name(): String {
        return "/流浪矿车"
    }

    override fun execute(event: MessageEvent) {
        val command = event.getCommand()
        val first = command.first
        val message: List<MessageItem> = when (first) {
            "","0"-> handleEmpty(command)
            "领养", "1" -> handleAdopt(command)
            "外出", "外出流浪", "流浪", "2" -> handleOut(command)
            "召回", "召回矿车", "回家", "3" -> handleRecall(command)
            "升级", "4" -> handleUpgrade(command)
            else -> listOf(
                createCommandImage(
                    """
                |未知参数，请参考以下指令: 
                | /流浪矿车 领养 
                | /流浪矿车 外出流浪 
                | /流浪矿车 召回流浪 
                | /流浪矿车 升级 
            """.trimIndent()
                ).toMessageImage()
            )
        }
        event.reply(*message.toTypedArray())
    }

    private fun handleUpgrade(command: CommandParser.CommandParserBean): List<MessageItem> {
        // TODO 检测是否复合升级条件
        return arrayListOf(
            combineImagesVertically(
                entityImage.toBufferedImage(),
                divider.toBufferedImage(),
                createCommandImage(
                    """
                |叮叮铛！指挥官您将矿车从 level 0 升级到了 level 1
                | 【特性】
                |· 矿车每次带回的科技最高可到 T2
                |· 矿产资源提升至 10%~23% (6900/3_0000)
            """.trimIndent()
                )
            ).toMessageImage()
        )
    }

    private fun handleRecall(command: CommandParser.CommandParserBean): List<MessageItem> {
        // TODO 计算可以获取的资源
        return arrayListOf(
            combineImagesVertically(
                entityImage.toBufferedImage(),
                divider.toBufferedImage(),
                createCommandImage(
                    """
                |叮叮铛！指挥官您将矿车召回了，看看他带来了哪些域外土特产吧！
                |您的矿车在外流浪了 0d:1h:0m:0s
                | 【TODO】
            """.trimIndent()
                )
            ).toMessageImage()
        )
    }


    private fun handleOut(command: CommandParser.CommandParserBean): List<MessageItem> {
        // TODO 修改矿车状态
        return arrayListOf(
            combineImagesVertically(
                entityImage.toBufferedImage(),
                divider.toBufferedImage(),
                createCommandImage(
                    """
                |叮叮铛！指挥官您将矿车派出流浪了，等他下次回来带来域外土特产吧！
                |
                |· 使用命令 “/流浪矿车 召回流浪/回家/召回矿车/3” 来将在外的矿车召回
                |· /流浪矿车 召回流浪
            """.trimIndent()
                )
            ).toMessageImage()
        )
    }

    private fun handleAdopt(command: CommandParser.CommandParserBean): ArrayList<MessageItem> {
        // TODO 修改矿车状态
        return arrayListOf(
            combineImagesVertically(
                entityImage.toBufferedImage(),
                divider.toBufferedImage(),
                createCommandImage(
                    """
                |叮叮铛！指挥官您在${area()}附近发现了一个野生的矿车
                |您打算将使用“矿脉的诱惑”将他引到您的前线作战指挥中心中
                |
                | 【科技解锁】
                | · 单位 矿车
                | 【特殊单位解锁】
                | · 流浪矿车
                |   > 无法被摧毁
                |   > 无法再生产
                |   > level 0 
                |   > 下次升级需要 T2 科技与 8888 资源点
            """.trimIndent()
                )
            ).toMessageImage()
        )
    }

    private fun area(): String {
        return when ((1..3).random()) {
            1 -> "野外矿场"
            2 -> "前线指挥作战中心前沿阵地"
            3 -> "商场"
            4 -> "油井"
            5 -> "宇宙中心无限岛"
            else -> "未知"
        }
    }

    private fun handleEmpty(command: CommandParser.CommandParserBean): ArrayList<MessageItem> {
        // TODO 检测矿车状态，自动选择 领养/外出/召回
        return arrayListOf(
            combineImagesVertically(
                image1.toBufferedImage(),
                divider.toBufferedImage(),
                createCommandImage(
                    """
                |指令: 
                | /流浪矿车 领养 
                | /流浪矿车 外出流浪 
                | /流浪矿车 召回流浪 
                | /流浪矿车 升级 
            """.trimIndent()
                )
            ).toMessageImage()
        )
    }

    private fun createCommandImage(text: String): BufferedImage {
        return addTextToAutoSizeImage(bgi, text, loadImage(image1).width)
    }
}