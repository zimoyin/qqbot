package io.github.zimoyin.qqbot.net.http

import io.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import io.github.zimoyin.qqbot.LocalLogger
import io.vertx.core.http.impl.headers.HeadersMultiMap
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions

/**
 *
 * @author : zimo
 * @date : 2024/05/12
 */
object TencentOpenApiHttpClient {
    private var isOptionsInitialized = false
    private val logger = LocalLogger(TencentOpenApiHttpClient::class.java)

    @JvmStatic
    val DefaultHeaders by lazy {
        HeadersMultiMap().apply {
            //通用头
        }
    }

    @JvmStatic
    var isSandBox = false
        set(value) {
            if (isOptionsInitialized) throw IllegalStateException("Options has been initialized. Please set up the sandbox environment before creating the bot")
            field = value
        }

    @JvmStatic
    var host = if (isSandBox) "sandbox.api.sgroup.qq.com" else "api.sgroup.qq.com"
        set(value) {
            if (isOptionsInitialized) throw IllegalStateException("Options has been initialized. Please set up the sandbox environment before creating the bot")
            field = value
            isCustomHost = true
            if (webSocketForwardingAddress == null) {
                webSocketForwardingAddress = "wss://${TencentOpenApiHttpClient.host}/websocket"
                logger.info("已自动设置 WebSocket 转发地址为：${webSocketForwardingAddress}")
            }
        }

    var webSocketForwardingAddress: String? = null
        set(value) {
            if (isOptionsInitialized) throw IllegalStateException("Options has been initialized. Please set up the sandbox environment before creating the bot")
            field = value
        }

    @JvmStatic
    var isCustomHost = false
        private set

    private val options: WebClientOptions by lazy {
        isOptionsInitialized = true
        WebClientOptions()
            .setUserAgent("java_qqbot_gf:0.0.1")
            .setDefaultHost(if (isSandBox) "sandbox.api.sgroup.qq.com" else "api.sgroup.qq.com")
            .setConnectTimeout(5000)
            .setKeepAlive(true)
            .setSsl(true)
            .setTrustAll(true)
            .setFollowRedirects(true)
            .setMaxRedirects(10)
            .setDefaultPort(443)
            .setPoolCleanerPeriod(5000)
            .setPoolEventLoopSize(32)
            .setMaxPoolSize(64)
            .setMaxWaitQueueSize(-1)
    }

    @JvmStatic
    val client: WebClient by lazy {
        WebClient.create(GLOBAL_VERTX_INSTANCE, options)
    }
}
