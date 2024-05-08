package io.github.zimoyin.qqbot.test.demo


import com.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.Bot
import com.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import com.github.zimoyin.qqbot.bot.message.type.ImageMessage
import com.github.zimoyin.qqbot.bot.onEvent
import com.github.zimoyin.qqbot.event.events.Event
import com.github.zimoyin.qqbot.event.events.message.MessageEvent
import com.github.zimoyin.qqbot.event.events.platform.MessageSendPreEvent
import com.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import com.github.zimoyin.qqbot.net.Intents
import com.github.zimoyin.qqbot.net.http.DefaultHttpClient
import com.github.zimoyin.qqbot.net.plus
import openDebug
import org.slf4j.LoggerFactory
import token
import java.io.File


@OptIn(UntestedApi::class)
suspend fun main() {
    openDebug()
    val logger = LoggerFactory.getLogger("Main")

    token.version = 1
//    token.version = 2

    //监听该BOT的全局事件
    GlobalEventBus.onBotEvent<Event>(token.appID) {
//        logger.debug("BOT全局事件监听: ${it.metadataType}")
    }

    //全局事件监听
    GlobalEventBus.onEvent<Event> {
        logger.debug("全局事件监听: ${it.metadataType}")
    }

    //拦截发送的信息
    MessageSendPreEvent.interceptor {
        // 如果含有拦截关键字，拦截
        if (it.messageChain.content().contains("拦截")) {
            return@interceptor it.apply {
                intercept = true
//                messageChain = MessageChainBuilder(messageChain.id).append("拦截该信息【新构建信息】").build()
                messageChain = MessageChainBuilder(it.messageChain).append("拦截该信息【修改源信息】").build()
            }
        }
        // 否则不拦截
        return@interceptor it
    }

    // TODO 添加企业机器人 IP 白名单提示
    // TODO 添加沙盒环境选项
    DefaultHttpClient.isSandBox = true
    Bot.createBot(token) {
        setIntents(Intents.Presets.PRIVATE_INTENTS)
//        setIntents(Intents.Presets.PRIVATE_GROUP_INTENTS)
    }.apply {
        onEvent<MessageEvent> {
            it.reply("你好".replace(".","∙"))
        }
        login().onSuccess {
            logger.info("BOT登录成功")
        }.onFailure {
            logger.error("BOT登录失败", it)
            GLOBAL_VERTX_INSTANCE.close()
        }
    }
}
