package com.github.zimoyin.qqbot.net.http.api

import com.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import io.vertx.core.http.impl.headers.HeadersMultiMap
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions

/**
 *
 * @author : zimo
 * @date : 2024/05/12
 */
object TencentOpenApiHttpClient {
    val DefaultHeaders by lazy {
        HeadersMultiMap().apply {
            //通用头
        }
    }
    var isSandBox = false
        set(value) {
            if (isOptionsInitialized) throw IllegalStateException("Options has been initialized. Please set up the sandbox environment before creating the bot")
            field = value
        }
    private var isOptionsInitialized = false
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

    val client: WebClient by lazy {
        WebClient.create(GLOBAL_VERTX_INSTANCE, options)
    }
}
