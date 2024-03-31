package io.github.zimoyin.qqbot.test.demo


import com.github.zimoyin.qqbot.bot.Bot
import com.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import com.github.zimoyin.qqbot.bot.onEvent
import com.github.zimoyin.qqbot.event.events.message.MessageEvent
import com.github.zimoyin.qqbot.net.Intents
import com.github.zimoyin.qqbot.net.bean.MessageMarkdown
import com.github.zimoyin.qqbot.net.bean.MessageMarkdownParam
import com.github.zimoyin.qqbot.utils.JSON
import kotlinx.coroutines.delay
import openDebug
import token
import java.lang.Thread.sleep
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


suspend fun main() {
    openDebug()

    token.version = 1
//    token.version = 2

    //监听该BOT的全局事件
//    GlobalEventBus.onBotEvent<Event>(token.appID) {
////        println("BOT全局事件监听: " + it.metadataType)
//    }
//
//    //全局事件监听
//    GlobalEventBus.onEvent<Event> {
//        println("全局事件监听: " + it.metadataType)
//    }
//
//    //拦截发送的信息
//    MessageSendPreEvent.interceptor {
//        return@interceptor it.apply {
//            intercept = false
//            messageChain = messageChain
//        }
//    }

    Bot.createBot(token) {
//        setIntents(github.zimoyin.net.Intents.Presets.PUBLIC_INTENTS)
        setIntents(Intents.Presets.PRIVATE_INTENTS)
    }.apply {
        println(this.botInfo)
        context.getRecord("key")
        //用于复用会话
        context["SESSION_ID"] = "60a176e1-2790-4bf0-85cd-c123763981ea"
        onEvent<MessageEvent> {
            val contact = it.windows
            it.reply("123").onSuccess {
                sleep(1000)
                contact.recall(it.id!!)
            }
            println("Bot -> " + it.messageChain.content())
        }
        login()
    }
}
