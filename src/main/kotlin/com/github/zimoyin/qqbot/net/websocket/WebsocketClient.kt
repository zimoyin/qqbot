package com.github.zimoyin.qqbot.net.websocket

import com.github.zimoyin.qqbot.bot.Bot
import com.github.zimoyin.qqbot.bot.BotSection
import com.github.zimoyin.qqbot.event.events.platform.bot.BotOfflineEvent
import com.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import com.github.zimoyin.qqbot.exception.WebSocketReconnectException
import com.github.zimoyin.qqbot.net.http.DefaultHttpClient
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.net.http.api.gatewayV2Async
import com.github.zimoyin.qqbot.net.websocket.handler.PayloadCmdHandler
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
import kotlin.math.log

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
        val gatewayURLByContent = bot.context.getString("gatewayURL")
        if (gatewayURLByContent != null) {
            //为了方便，在没有分片的情况下使用默认的硬编码的URL。但是可能回出现BUG，因为这是一个不再维护的使用
            if (bot.config.shards != BotSection()) logger.warn("自定义WSS接入点的分片非默认值")
            if (!DefaultHttpClient.isSandBox) logger.warn("当前环境不是沙盒环境，请将环境设置为沙盒环境 > DefaultHttpClient.isSandBox = true")
            logger.warn("你正在使用自定义WSS接入点请在正式环境中停止使用，否则可能会导致不可预测的BUG: $gatewayURLByContent")
            bot.context["shards"] = 1
            gatewayURL = gatewayURLByContent
        } else {
            val gateway = HttpAPIClient.gatewayV2Async(bot.config.token).await()
            gateway.getInteger("code")?.apply {
                check(gateway.getString("message") != "Token错误") { "无法获取到登录点,原因为 Token / AppID / Secret 错误" }
                check(gateway.getString("message") != "接口访问源IP不在白名单") { "无法获取到登录点,原因为当前环境为正式环境(非沙盒环境)，请将当前服务器IP 添加到QQ开发平台的IP白名单中。或者设置环境为沙盒环境(测试环境) > DefaultHttpClient.isSandBox = true" }
                throw IllegalStateException("无法获取到登录点,原因为: " + gateway.getString("message"))
            }
            bot.context["shards"] = gateway.getInteger("shards") ?: 1 //推荐分片数
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
                        when (it) {
                            // 接收到处理器发出的重连申请
                            is WebSocketReconnectException -> {
                                logger.debug("WebSocket[${ws.hashCode()}]  准备重连 -> HttpClosedException: ${it.message}")
                                //广播机器人下线事件
                                GlobalEventBus.broadcast(
                                    BotOfflineEvent(
                                        bot.botInfo,
                                        bot.context.get<Throwable>(throwableKey)
                                    )
                                )
                                reconnect(client, reconnect, retry + 1)
                            }

                            else -> logger.error(
                                "WebSocket[${ws.hashCode()}]  位于网络层的阻塞线程捕获到了处理器的异常，这是不合理的！以下为捕获的异常",
                                it
                            )
                        }
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
        logger.debug("WebSocketClient[${client.hashCode()}] 剩余重连次数: $retry")
        logger.debug("WebSocketClient[${client.hashCode()}] 是否允许重连: $reconnect")
        if (retry > 0 && reconnect) {
            reconnectHandler(client, retry).handle(0)
        } else {
            logger.warn("WebSocketClient[${client.hashCode()}] 重连达到极限，不再允许重连")
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

    /**
     * 关闭 WebSocketClient
     */
    fun close() {
        bot.context.get<WebSocket>("ws")?.close()
        client?.close()
    }
}
