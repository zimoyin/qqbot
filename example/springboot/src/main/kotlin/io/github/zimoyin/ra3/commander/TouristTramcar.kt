package io.github.zimoyin.ra3.commander

import io.github.zimoyin.qqbot.bot.message.type.MessageItem
import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import io.github.zimoyin.ra3.aspect.RegisterAOP
import io.github.zimoyin.ra3.config.ResourcesReleaseConfig
import io.github.zimoyin.ra3.expand.*
import io.github.zimoyin.ra3.service.PlayerCarService
import io.github.zimoyin.ra3.utils.ImageUtil.loadImage
import io.github.zimoyin.ra3.utils.ImageUtilEx.Companion.addTextToAutoSizeImage
import io.github.zimoyin.ra3.utils.ImageUtilEx.Companion.combineImagesVertically
import io.github.zimoyin.ra3.utils.RandomUtil
import org.springframework.stereotype.Component
import java.awt.image.BufferedImage
import java.io.File
import java.time.Instant
import java.time.ZoneId

/**
 *
 * @author : zimo
 * @date : 2025/01/05
 */
@Component
class TouristTramcar(
    val service: PlayerCarService
) : AbsCommander<MessageEvent>() {
    companion object{
        const val MONEY_MAX = 3_0000
    }
    val entityImage by lazy { File(config.targetPath, ResourcesReleaseConfig.CAMP_2_ENTITY_TRAMCAR_1_IMAGE) }
    val bgi by lazy { File(config.targetPath, ResourcesReleaseConfig.COMMAND_BACKGROUND_IMAGE) }
    val divider by lazy { File(config.targetPath, ResourcesReleaseConfig.DIVIDER_IMAGE) }
    val image1 by lazy { File(config.targetPath, ResourcesReleaseConfig.TOURIST_TRAMCAR_IMAGE) }

    override fun name(): String {
        return "/流浪矿车"
    }

    @RegisterAOP
    override fun execute(event: MessageEvent) {
        val command = event.getCommand()
        val first = command.first
        val message: List<MessageItem> = when (first) {
            "", "0" -> handleEmpty(event)
            "领养", "1" -> handleAdopt(event)
            "外出", "外出流浪", "流浪", "2" -> handleOut(event)
            "召回", "召回矿车", "回家", "3" -> handleRecall(event)
            "升级", "4" -> handleUpgrade(event)
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

    private fun handleUpgrade(event: MessageEvent): List<MessageItem> {
        // TODO 检测是否复合升级条件
        val car = service.getPlayerCar(event.sender.id)
        val level = car.level
        return arrayListOf(
            combineImagesVertically(
                entityImage.toBufferedImage(),
                divider.toBufferedImage(),
                createCommandImage(
                    """
                |叮叮铛！指挥官您将矿车从 level $level 升级到了 level ${(level+1)}
                | 【特性】
                |· 矿车每次带回的科技最高可到 T$level
                |· 矿产资源提升
            """.trimIndent()
                )
            ).toMessageImage()
        )
    }

    private fun handleRecall(event: MessageEvent): List<MessageItem> {
        // 外出时间
        val time = System.currentTimeMillis() - service.returned(event.sender.id)
        val car = service.getPlayerCar(event.sender.id)
        val level = car.level

        val money = (time / MONEY_MAX * (level + 0.1)).toInt()
        // TODO 随机一个单位/科技出现
        val entityResult = if (RandomUtil.randomBoolean(0.3))"天狗机器人" else "无"
        val result = if (RandomUtil.randomBoolean(0.3)) "天狗机器人" else "无"
        return arrayListOf(
            combineImagesVertically(
                entityImage.toBufferedImage(),
                divider.toBufferedImage(),
                createCommandImage(
                    """
                |叮叮铛！指挥官您将矿车召回了，看看他带来了哪些域外土特产吧！
                |您的矿车在外流浪了 ${Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDateTime()}
                | 【资金】$money
                | 【科技】$result
                | 【单位】$entityResult
            """.trimIndent()
                )
            ).toMessageImage()
        )
    }


    private fun handleOut(event: MessageEvent): List<MessageItem> {
        if (!service.egress(event.sender.id)) {
            logger.error("command: /流浪矿车 外出流浪 无法派出（uid ${event.sender.id}）")
            arrayListOf(
                combineImagesVertically(
                    createCommandImage(
                        """
                |非常抱歉指挥官，现在您的矿车无法派出流浪
            """.trimIndent()
                    )
                ).toMessageImage()
            )
        }
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

    private fun handleAdopt(event: MessageEvent): ArrayList<MessageItem> {
        if (!service.create(event.sender.id)) {
            arrayListOf(
                combineImagesVertically(
                    createCommandImage(
                        """
                |非常抱歉现在您无法领养矿车，请稍后再试
                |· /流浪矿车 领养
            """.trimIndent()
                    )
                ).toMessageImage()
            )
        }
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

    private fun handleEmpty(event: MessageEvent): List<MessageItem> {
        return arrayListOf(
            combineImagesVertically(
                image1.toBufferedImage(),
                divider.toBufferedImage(),
                createCommandImage(
                    """
                |指令: 
                | /流浪矿车 领养 (*)
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