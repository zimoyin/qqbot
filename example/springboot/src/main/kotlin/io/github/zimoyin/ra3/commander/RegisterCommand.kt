package io.github.zimoyin.ra3.commander

import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.message.type.ImageMessage
import io.github.zimoyin.qqbot.bot.message.type.PlainTextMessage
import io.github.zimoyin.ra3.annotations.Commander
import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import io.github.zimoyin.ra3.aspect.RegisterAOP
import io.github.zimoyin.ra3.config.ResourcesReleaseConfig
import io.github.zimoyin.ra3.expand.toMessageImage
import io.github.zimoyin.ra3.service.CommandParser
import io.github.zimoyin.ra3.service.IRegisterService
import io.github.zimoyin.ra3.utils.ImageUtilEx.Companion.combineImagesVertically
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File

/**
 *
 * @author : zimo
 * @date : 2025/01/03
 */
@Component
open class RegisterCommand(
    val service: IRegisterService,
) : AbsCommander<MessageEvent>() {
    val image1 by lazy { File(config.targetPath, ResourcesReleaseConfig.CAMP_1_IMAGE) }
    val image2 by lazy { File(config.targetPath, ResourcesReleaseConfig.CAMP_2_IMAGE) }
    val image3 by lazy { File(config.targetPath, ResourcesReleaseConfig.CAMP_3_IMAGE) }

    override fun name(): String {
        return "/注册"
    }

    override fun execute(event: MessageEvent) {
        register(event)
    }


    @Commander(name = "/注销")
    fun unregister(event: MessageEvent) {
        val uid = event.sender.id
        // TODO 使用定时任务清理资产
        if (service.unregister(uid) > 0) {
            event.reply(
                PlainTextMessage("您已成功注销，请重新注册")
            )
        } else {
            event.reply(
                PlainTextMessage("注销失败,您可能还未注册")
            )
        }
    }

    /**
     * 注册命令
     */
    @OptIn(UntestedApi::class)
    fun register(event: MessageEvent) {
        val uid = event.sender.id
        val nick = event.sender.nick
        val parse = CommandParser.parse(event)
        val param = parse.params.lastOrNull()
        logger.debug("注册命令：{}", parse)
        if (service.isRegistered(uid)) {
            event.reply(
                PlainTextMessage("欢迎回来指挥官！您已经在籍了\n请输入 /流浪矿车 来开始你的征战吧")
            )
            return
        }
        if (param == null) {
            event.reply(
                combineImagesVertically(image1, image2, image3).toMessageImage(),
                PlainTextMessage("请选择你的阵营（1/2/3）\n请回复：@机器人 /注册 1/2/3")
            )
        } else {
            kotlin.runCatching {
                val image = when (param.toInt()) {
                    1 -> image1
                    2 -> image2
                    3 -> image3
                    else -> throw IllegalArgumentException("参数错误")
                }
                if (service.register(uid, nick, param.toInt())) {
                    event.reply(
                        ImageMessage.create(image),
                        PlainTextMessage("欢迎回来，${nick} 指挥官!\n请输入 /流浪矿车 来开始你的征战吧")
                    )
                } else {
                    event.reply(
                        ImageMessage.create(image),
                        PlainTextMessage("欢迎回来，${nick} 指挥官!\n您无需再次注册\n请输入 /流浪矿车 来开始你的征战吧")
                    )
                }
            }.onFailure {
                event.reply("注册失败，请重试")
                logger.error("注册失败", it)
            }
        }
    }

}