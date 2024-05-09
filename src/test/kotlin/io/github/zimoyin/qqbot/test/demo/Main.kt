package io.github.zimoyin.qqbot.test.demo


import com.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.Bot
import com.github.zimoyin.qqbot.bot.message.type.ImageMessage
import com.github.zimoyin.qqbot.bot.onEvent
import com.github.zimoyin.qqbot.event.events.Event
import com.github.zimoyin.qqbot.event.events.message.MessageEvent
import com.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import com.github.zimoyin.qqbot.net.Intents
import com.github.zimoyin.qqbot.net.http.DefaultHttpClient
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
        it.messageChain.filter { it is ImageMessage }.map { it as ImageMessage }.forEach {
            println(it.attachment)
        }
    }

    // TODO 频道以URL形式发送图片
    // TODO 群聊富文本
    DefaultHttpClient.isSandBox = true
    Bot.createBot(token) {
//        setIntents(Intents.Presets.PRIVATE_INTENTS)
        setIntents(Intents.Presets.PRIVATE_GROUP_INTENTS)
    }.apply {
        onEvent<MessageEvent> {
            it.reply("http:test.cn").onFailure {
                it.printStackTrace()
            }
            it.reply("http:test.com")
        }
        login().onSuccess {
            logger.info("BOT登录成功")
        }.onFailure {
            logger.error("BOT登录失败", it)
            GLOBAL_VERTX_INSTANCE.close()
        }
    }
}
