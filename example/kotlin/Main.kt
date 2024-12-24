package example.kotlin


import io.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.Bot
import io.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import io.github.zimoyin.qqbot.bot.message.type.ImageMessage
import io.github.zimoyin.qqbot.bot.onEvent
import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import io.github.zimoyin.qqbot.event.events.platform.MessageSendPreEvent
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import io.github.zimoyin.qqbot.net.Intents
import io.github.zimoyin.qqbot.net.http.DefaultHttpClient
import io.github.zimoyin.qqbot.net.plus
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

    DefaultHttpClient.isSandBox = true
    Bot.createBot(token) {
//        setIntents(Intents.Presets.PRIVATE_INTENTS)
        setIntents(Intents.Presets.PRIVATE_GROUP_INTENTS)
//        setIntents(0)
    }.apply {
        //用于复用会话
//        context["SESSION_ID"] = "60a176e1-2790-4bf0-85cd-c123763981ea"
//        context["SESSION_ID_Failure_Reconnection"] = true // 会话ID 过去则重连
        context["gatewayURL"] = "wss://sandbox.api.sgroup.qq.com/websocket/" // 硬编码设置wss接入点同时shards设置为1.不推荐使用
        // 内部日志打印细节
        context["PAYLOAD_CMD_HANDLER_DEBUG_LOG"] = false // 命令处理器日志
        context["PAYLOAD_CMD_HANDLER_DEBUG_MATA_DATA_LOG"] = false // 命令元数据日志
        context["PAYLOAD_CMD_HANDLER_DEBUG_HEART_BEAT"] = false // 心跳日志,不能单独开启应该与上面两个其中一个一并开启

        onEvent<MessageEvent> {
//           val c =  MessageChainBuilder(it.msgID).apply {
//                append(ImageMessage.create(File("C:\\Users\\zimoa\\Pictures\\QQ图片20240313163158.jpg")))
//                append("图片来了")
//            }.build()
//            it.reply(c)

//            it.reply(context.toString())
            it.reply("你好".replace(".","∙"))
        }
        login().onSuccess {
            logger.info("BOT登录成功")
        }.onFailure {
            logger.error("BOT登录失败", it)
            GLOBAL_VERTX_INSTANCE.close()
        }
        context.getRecord("vertx")?.apply {
            logger.debug("机器人所在 vertx 添加到上下文时候的最后一次位置: {}", this)
        }
        context["BOT_INFO"] = this.botInfo
        logger.debug("机器人上下文信息: {}", context)
    }
}
