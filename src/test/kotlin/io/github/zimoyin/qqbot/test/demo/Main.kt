package io.github.zimoyin.qqbot.test.demo


import com.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.Bot
import com.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import com.github.zimoyin.qqbot.bot.message.type.ImageMessage
import com.github.zimoyin.qqbot.bot.message.type.ProactiveAudioMessage
import com.github.zimoyin.qqbot.bot.message.type.ProactiveMediaMessage
import com.github.zimoyin.qqbot.bot.message.type.ProactiveVideoMessage
import com.github.zimoyin.qqbot.bot.onEvent
import com.github.zimoyin.qqbot.event.events.Event
import com.github.zimoyin.qqbot.event.events.message.MessageEvent
import com.github.zimoyin.qqbot.event.events.platform.MessageSendPreEvent
import com.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import com.github.zimoyin.qqbot.net.Intents
import com.github.zimoyin.qqbot.net.bean.MediaMessageBean
import com.github.zimoyin.qqbot.net.bean.SendMediaBean
import com.github.zimoyin.qqbot.net.http.DefaultHttpClient
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.net.http.api.group.uploadMediaToGroup
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
            val c =  MessageChainBuilder(it.msgID).apply {
//                append(ImageMessage.create("gchat.qpic.cn/qmeetpic/647974764018581392/652108710-2598629258-10CCE5CC13F010378D8CED891C529E4C/0"))
               append(ProactiveVideoMessage.create("https://cdn.pixabay.com/video/2023/08/11/175587-853887900_large.mp4"))
                val a = HttpAPIClient.uploadMediaToGroup(group_id,token, SendMediaBean)
                append(ProactiveMediaMessage.create(MediaMessageBean))
                append("图片来了")
            }.build()
//            it.reply("你好".replace(".","∙"))
            it.reply(c)
        }
        login().onSuccess {
            logger.info("BOT登录成功")
        }.onFailure {
            logger.error("BOT登录失败", it)
            GLOBAL_VERTX_INSTANCE.close()
        }
    }
}
