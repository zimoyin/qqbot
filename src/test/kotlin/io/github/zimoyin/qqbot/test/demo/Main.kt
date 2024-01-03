package io.github.zimoyin.qqbot.test.demo


import com.github.zimoyin.qqbot.bot.Bot
import com.github.zimoyin.qqbot.bot.onEvent
import com.github.zimoyin.qqbot.event.events.Event
import com.github.zimoyin.qqbot.event.events.message.MessageEvent
import com.github.zimoyin.qqbot.event.events.platform.MessageSendPreEvent
import com.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import com.github.zimoyin.qqbot.net.Intents
import com.github.zimoyin.qqbot.utils.ex.await
import openDebug
import token


suspend fun main() {
    openDebug()

    token.version = 1
//    token.version = 2

    //监听该BOT的全局事件
    GlobalEventBus.onBotEvent<Event>(token.appID) {
//        println("BOT全局事件监听: " + it.metadataType)
    }

    //全局事件监听
    GlobalEventBus.onEvent<Event> {
        println("全局事件监听: " + it.metadataType)
    }

    //拦截发送的信息
    MessageSendPreEvent.interceptor {
        return@interceptor it.apply {
            intercept = false
            messageChain = messageChain
        }
    }

    Bot.INSTANCE.createBot(token) {
//        setIntents(github.zimoyin.net.Intents.Presets.PUBLIC_INTENTS)
        setIntents(Intents.Presets.PRIVATE_INTENTS)
    }.apply {
        println(this.botInfo)
        //用于复用会话
//    context["SESSION_ID"] = "51415469-4672-41c2-a72c-a3038f4b4cf1"
        onEvent<MessageEvent> {

            it.getBot().getGuilds().await().forEach {
                println(it)
            }

            println("Bot -> " + it.messageChain.toString())
            println("Bot -> " + it.messageChain.id)
            //尝试发送信息
            it.reply(it.messageChain)
        }
//        login()

        getGuilds().await().forEach {
            println(it.guildID)
            it.getChannel("634326197").onSuccess {
                println(it)
                it!!.assetManagement.deleteEssentialMessage("08b0c3be83acc3f391db0110b599bcae02383b48e2fdd3ac06")
                    .onSuccess {
                        println(it)
                    }.onFailure {
                    it.printStackTrace()
                }
            }
//            it.getChannels().await().forEach {c->
//                c.assetManagement.deleteEssentialMessage("08b0c3be83acc3f391db0110b599bcae02383b48e2fdd3ac06").onSuccess {
//                    println(it)
//                }.onFailure {
//                    it.printStackTrace()
//                }
//            }
        }
    }
}
