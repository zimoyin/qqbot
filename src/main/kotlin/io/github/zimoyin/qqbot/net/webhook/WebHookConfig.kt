package io.github.zimoyin.qqbot.net.webhook

import io.vertx.core.http.HttpServerOptions
import io.vertx.core.net.JksOptions
import io.vertx.core.net.KeyStoreOptions
import io.vertx.core.net.PemKeyCertOptions
import io.vertx.core.net.PfxOptions
import jdk.internal.org.jline.utils.Colors.s
import java.io.File
import java.util.*

/**
 *
 * @author : zimo
 * @date : 2024/12/21
 */
data class WebHookConfig(
    /**
     * SSL 文件路径
     */
    val sslPath: String = "./",
    /**
     * SSL 密码，如果没有则留空
     */
    val password: String = "",
    /**
     * HttpServerOptions 实例
     */
    val options: HttpServerOptions = HttpServerOptions().apply {
        isSsl = true
        sslHandshakeTimeout = 30L
        webSocketClosingTimeout = 30
        sslOptions.isUseAlpn = false
        webSocketCompressionLevel = 6
        webSocketSubProtocols = arrayListOf("wss", "ws")

        require(loadCert(this, sslPath, password)) {
            IllegalArgumentException("Could not find a valid SSL certificate in the path $sslPath")
        }
    },
    /**
     * WebHook Server 端口，如果为 0 则是随机端口
     */
    val port: Int = 443,
    /**
     * WebHook Server 监听地址
     */
    val host: String = "0.0.0.0",
    /**
     * 是否启用 WebSocket 转发
     */
    val enableWebSocketForwarding: Boolean = false,
    /**
     * WebSocket 暴露转发路径
     */
    val webSocketPath: String = "/websocket",
    /**
     * 是否启用 WebSocket 转发登录验证
     * 客户端与腾讯服务器鉴权流程一直，不同的是 WebHook 转发的 WebSocket 有两种鉴权方式，将根据 Token 完整度自动使用
     */
    val enableWebSocketForwardingLoginVerify: Boolean = true,
) {
    companion object {
        @JvmStatic
        @JvmOverloads
        fun createBySslPath(sslPath: String, password: String = ""): WebHookConfig {
            return WebHookConfig(sslPath, password)
        }

        /**
         * 自动加载证书
         * @param options HttpServerOptions 实例
         * @param sslPath SSL 文件路径
         * @return 是否成功加载证书
         */
        private fun loadCert(options: HttpServerOptions, sslPath: String, password: String): Boolean {
            val sslDir = File(sslPath)
            if (!sslDir.exists() || !sslDir.isDirectory) return false

            // 检查是否存在 PEM 文件（key 和 cert）
            val keyFile = sslDir.listFiles { _, name -> name.endsWith(".key") }?.firstOrNull()
            val crtFile = sslDir.listFiles { _, name -> name.endsWith(".crt") }?.firstOrNull()
            val pemFile = sslDir.listFiles { _, name -> name.endsWith(".pem") } ?: emptyArray()
            if (keyFile != null && (crtFile != null || pemFile.isEmpty())) {
                options.keyCertOptions = PemKeyCertOptions()
                    .setKeyPath(keyFile.absolutePath)
                    .setCertPath(pemFile.firstOrNull()?.absolutePath ?: crtFile?.absolutePath)
                return true
            }

            // 检查是否存在 JKS 文件
            val jksFile = sslDir.listFiles { _, name -> name.endsWith(".jks") }?.firstOrNull()
            if (jksFile != null) {
                options.keyCertOptions = JksOptions()
                    .setPath(jksFile.absolutePath)
                    .setPassword(password) // 替换为 JKS 密码
                if (password.isEmpty()) throw IllegalArgumentException("JKS password is empty.")
                return true
            }

            // 检查是否存在 PFX 文件
            val pfxFile = sslDir.listFiles { _, name -> name.endsWith(".pfx") }?.firstOrNull()
            if (pfxFile != null) {
                options.keyCertOptions = PfxOptions()
                    .setPath(pfxFile.absolutePath)
                    .setPassword(password) // 替换为 PFX 密码
                if (password.isEmpty()) throw IllegalArgumentException("PFX password is empty.")
                return true
            }

            // pem 文件
            if (pemFile.size >= 2) {
                kotlin.runCatching {
                    options.keyCertOptions = PemKeyCertOptions()
                        .setKeyPath(pemFile.first { it.readText().contains("PRIVATE", true) }.absolutePath)
                        .setCertPath(pemFile.first { it.readText().contains("PRIVATE", true).not() }.absolutePath)
                    return true
                }
            }

            return false
        }
    }
}
