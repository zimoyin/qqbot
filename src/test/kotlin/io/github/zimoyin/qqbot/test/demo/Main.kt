package io.github.zimoyin.qqbot.test.demo


import com.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.Bot
import com.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import com.github.zimoyin.qqbot.bot.message.type.ImageMessage
import com.github.zimoyin.qqbot.bot.message.type.PlainTextMessage
import com.github.zimoyin.qqbot.bot.onEvent
import com.github.zimoyin.qqbot.event.events.Event
import com.github.zimoyin.qqbot.event.events.message.MessageEvent
import com.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import com.github.zimoyin.qqbot.net.Intents
import com.github.zimoyin.qqbot.net.http.api.TencentOpenApiHttpClient
import openDebug
import org.slf4j.LoggerFactory
import token

@OptIn(UntestedApi::class)
suspend fun main() {
    openDebug()
    val logger = LoggerFactory.getLogger("Main")

    token.version = 1
//    token.version = 2

    //全局事件监听
    GlobalEventBus.onEvent<Event> {
        logger.debug("全局事件监听: ${it.metadataType}")
    }
    GlobalEventBus.onEvent<MessageEvent> {
        logger.debug(it.messageChain.toString())
        it.messageChain.filter { it is ImageMessage }.map { it as ImageMessage }.forEach {
            println(it.attachment)
        }
        val first = it.messageChain.filter {
            it is PlainTextMessage
        }.map {
            it as PlainTextMessage
        }.first().content
        println(first)

    }

    TencentOpenApiHttpClient.isSandBox = false
    Bot.createBot(token) {
//        setIntents(Intents.Presets.PRIVATE_INTENTS)
        setIntents(Intents.Presets.PRIVATE_GROUP_INTENTS)
    }.apply {
        config.retry = 99
//        context["SESSION_ID"] = "d5141070-a591-47fa-b334-8ed1eff92ec6"
        context["PAYLOAD_CMD_HANDLER_DEBUG_LOG"] = false // 命令处理器日志
        context["PAYLOAD_CMD_HANDLER_DEBUG_MATA_DATA_LOG"] = false // 命令元数据日志
        context["PAYLOAD_CMD_HANDLER_DEBUG_HEART_BEAT"] = false // 心跳日志,不能单独开启应该与上面两个其中一个一并开启
        onEvent<MessageEvent> {
            val chain = MessageChainBuilder().setID(it.msgID)
                .append(ImageMessage.create("http://ts1.cn.mm.bing.net/th/id/R-C.23034dbcaded6ab4169b9514f76f51b5?rik=mSGADwV9o/teUA&riu=http://pic.bizhi360.com/bbpic/40/9640_1.jpg&ehk=RYei4n5qyNCPVysJmE2a3WhxSOXqGQMGJcvWBmFyfdg=&risl=&pid=ImgRaw&r=0"))
//                .append(ImageMessage.create(File("C:\\Users\\zimoa\\Pictures\\82459221_p0.jpg")))
                .build()
//            it.reply(it.messageChain)
            it.reply(chain)//
        }

        login().onSuccess {
            logger.info("BOT登录成功")
        }.onFailure {
            logger.error("BOT登录失败", it)
            GLOBAL_VERTX_INSTANCE.close()
        }
    }
}
