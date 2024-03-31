package io.github.zimoyin.qqbot.test.demo


import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.Bot
import com.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import com.github.zimoyin.qqbot.bot.message.type.ImageMessage
import com.github.zimoyin.qqbot.bot.onEvent
import com.github.zimoyin.qqbot.event.events.message.MessageEvent
import com.github.zimoyin.qqbot.net.Intents
import openDebug
import token
import java.io.File


@OptIn(UntestedApi::class)
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
//            intercept = true
//            messageChain = MessageChainBuilder(messageChain.id).append("修改后").build()
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
           val c =  MessageChainBuilder(it.msgID).apply {
                append(ImageMessage.create(File("C:\\Users\\zimoa\\Pictures\\QQ图片20240313163158.jpg")))
                append("图片来了")
            }.build()
//            it.reply(it.messageChain).onSuccess {
//                println(it.content())
//            }.onFailure {
//                it.printStackTrace()
//            }

            it.reply(c)
        }
        login()
    }
}
