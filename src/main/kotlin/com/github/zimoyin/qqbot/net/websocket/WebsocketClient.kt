package com.github.zimoyin.qqbot.net.websocket

import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.net.websocket.handler.PayloadCmdHandler
import com.github.zimoyin.qqbot.bot.Bot
import com.github.zimoyin.qqbot.bot.BotSection
import com.github.zimoyin.qqbot.event.events.platform.bot.BotOfflineEvent
import com.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import com.github.zimoyin.qqbot.net.http.api.gatewayV2Async
import com.github.zimoyin.qqbot.utils.ex.await
import com.github.zimoyin.qqbot.utils.ex.executeBlockingKt
import com.github.zimoyin.qqbot.utils.ex.toUrl
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.http.HttpClosedException
import io.vertx.core.http.WebSocket
import io.vertx.core.http.WebSocketClient
import io.vertx.core.http.WebSocketClientOptions
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.slf4j.LoggerFactory
import java.net.InetAddress
import java.net.SocketException

private const val s = "internal.handler"

/**
 *
 * @author : zimo
 * @project : qqbot_gf
 * @date : 2023/12/06/18:30
 * @description ：
 */
//class WebsocketClient(private val bot: Bot) : AbstractVerticle() {
class WebsocketClient(private val bot: Bot) : CoroutineVerticle() {
    private val logger = LoggerFactory.getLogger(WebsocketClient::class.java)
    private val promise: Promise<WebSocket> = bot.context.getValue<Promise<WebSocket>>("internal.promise")
    private var reconnectTime: Long = 1 * 1000
    private var gatewayURL: String? = null
    private var client: WebSocketClient? = null
    private val headerCycle: Long = 10 * 1000
    private val handlerKey = "internal.handler"
    private val throwableKey = "internal.throwable"

    override suspend fun start() {
        //配置 ws
        val options = WebSocketClientOptions()
            .setConnectTimeout(6000)
//            .setWriteIdleTimeout((headerCycle / 1000 + 10).toInt()) //如果心跳在 10 s 内没有发送出去就抛出异常
            .setSsl(true)
            .setTrustAll(true)
        client = vertx.createWebSocketClient(options)
        logger.info("WebSocketClient[${client.hashCode()}] 配置完成 -> 绑定 Bot AppID : ${bot.config.token.appID}")
        logger.debug("心跳周期为: ${headerCycle / 1000.0}s")
        //准备 服务器地址
        if (bot.config.shards == BotSection()) {
            bot.context["shards"] = 1
            //为了方便，在没有分片的情况下使用默认的硬编码的URL，如果未来发生变动的时候需要手动修改代码
            gatewayURL = "wss://api.sgroup.qq.com/websocket/"
        } else {
            val gateway = HttpAPIClient.gatewayV2Async(bot.config.token).await()
            bot.context["shards"] = gateway.getInteger("shards") //推荐分片数
            //获取 ws 地址
            gatewayURL = gateway.getString("url")
            gatewayURL ?: throw NullPointerException("无法获取到 WSS 接入点")

        }
        bot.context["internal.headerCycle"] = bot.context.getOrDefault<Long>("internal.headerCycle", headerCycle)
        connect(client!!, bot.config.reconnect, bot.config.retry)
    }

    /**
     * @param reconnect 是否允许重连
     * @param retry 自动重连次数
     */
    private fun connect(client: WebSocketClient, reconnect: Boolean = false, retry: Int = 8) {
        if (gatewayURL == null) throw NullPointerException("gateway 无法获取到URL")
        bot.context["vertx"] = vertx

        logger.debug("WebSocketClient[${client.hashCode()}] 准备访问WebSocketSever接入点: $gatewayURL")
        val url = gatewayURL!!.replace("wss://", "https://", true).toUrl()

        //开启 ws
        client.connect(443, url.host, url.path).onSuccess { ws ->
            bot.context["ws"] = ws
            if (!bot.context.contains(handlerKey)) bot.context[handlerKey] = PayloadCmdHandler(bot)
            logger.info("WebSocketClient[${client.hashCode()}] 完成创建 WebSocket[${ws.hashCode()}] : 链接服务器成功")
        }.onSuccess { ws ->
            val handler = bot.context.getValue<PayloadCmdHandler>(handlerKey)


            ws.handler { buffer ->
                vertx.executeBlockingKt {
                    kotlin.runCatching {
                        handler.handle(buffer)
                    }.onFailure {
                        if (it is HttpClosedException) {
                            logger.debug("WebSocket[${ws.hashCode()}]  准备重连 -> HttpClosedException: ${it.message}")
                            //广播机器人下线事件
                            GlobalEventBus.broadcast(
                                BotOfflineEvent(
                                    bot.botInfo,
                                    bot.context.get<Throwable>(throwableKey)
                                )
                            )
                            reconnect(client, reconnect, retry)
                        } else logger.error(
                            "WebSocket[${ws.hashCode()}]  位于网络层的阻塞线程捕获到了处理器的异常，这是不合理的！以下为捕获的异常",
                            it
                        )
                    }
                }
            }

            ws.closeHandler {
                handler.close()
                logger.info("WebSocketClient[${client.hashCode()}] 被关闭")
                //广播机器人下线事件
                GlobalEventBus.broadcast(BotOfflineEvent(bot.botInfo, bot.context.get(throwableKey)))
            }

            ws.exceptionHandler {
                bot.context[throwableKey] = it
                when (it) {
                    is SocketException -> logger.error("WebSocket链接发生异常 (java.net.SocketException)", it)

                    is HttpClosedException ->
                        logger.error("WebSocket链接因为异常被关闭 (io.vertx.core.http.HttpClosedException)", it)

                    else -> logger.error("WebSocket连接出现异常 ", it)
                }

                if (it is SocketException) reconnect(client, reconnect, retry)
            }
        }.onFailure {
            logger.warn("WebSocketClient[${client.hashCode()}] 启动失败,由于没有建立连接不予重连，请新建连接并保证网络畅通")
            logger.error("WebSocketClient[${client.hashCode()}] 启动失败", it)
            promise.fail(it)
        }
    }


    private fun reconnect(client: WebSocketClient, reconnect: Boolean, retry: Int) {
        if (retry > 0 && reconnect) {
            reconnectHandler(client, retry).handle(0)
        }
    }

    private fun reconnectHandler(client: WebSocketClient, retry: Int, count: Int = 0): Handler<Long> {
        return Handler<Long> {
            vertx.executeBlockingKt {
                //检测网络是否通畅
                if (ping("223.5.5.5") || ping("114.114.114.114") || ping("8.8.8.8") || ping("208.67.222.222")) {
                    logger.debug("WebSocketClient[${client.hashCode()}] 重连中...")
                    connect(client, true, retry - 1)
                } else {
                    logger.warn("WebSocketClient[${client.hashCode()}] 网络不可用, 预计将在 ${reconnectTime}ms 后尝试重连[网络检测次数:$count]")
                    if (reconnectTime + 100 <= 3500) {
                        reconnectTime += 100
                    }
                    vertx.setTimer(reconnectTime, reconnectHandler(client, retry, count + 1))
                }
            }
        }
    }

    private fun ping(host: String = "223.5.5.5", port: Int = 53): Boolean {
        runCatching {
            val serverAddress = InetAddress.getByName(host)
            if (serverAddress.isReachable(port)) {
                return true // 如果可以到达，则认为网络可用
            }
        }
        return false // 如果有任何错误发生，假定网络不可用
    }

    fun close() {
        bot.context.get<WebSocket>("ws")?.close()
        client?.close()
    }
}
