package io.github.zimoyin.qqbot.net.webhook

import io.github.zimoyin.qqbot.SystemLogger
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.net.JksOptions
import io.vertx.core.net.PemKeyCertOptions
import io.vertx.core.net.PfxOptions
import java.io.File

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
     * 是否启用 SSL
     */
    val isSSL: Boolean = true,
    /**
     * HttpServerOptions 实例
     */
    val options: HttpServerOptions = createHttpServerOptions(sslPath,isSSL),
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

    /**
     * 是否启用 WebSocket 转发订阅验证，该功能是实验性的
     * 开启后会根据 WS 客户端的 Intents 来转发被订阅的事件
     */
    val enableWebSocketForwardingIntentsVerify:Boolean = false,
) {
    companion object {
        @JvmStatic
        fun builder(): WebHookConfigBuilder = WebHookConfigBuilder()

        @JvmStatic
        @JvmOverloads
        fun createBySslPath(sslPath: String, password: String = ""): WebHookConfig {
            return WebHookConfig(sslPath, password)
        }

        @JvmStatic
        @JvmOverloads
        fun createHttpServerOptions(
            sslPath: String = "",
            isSSL: Boolean = sslPath.isNotEmpty(),
            password: String = ""
        ): HttpServerOptions {
            return HttpServerOptions().apply {
                isSsl = isSSL
                sslHandshakeTimeout = 30L
                webSocketClosingTimeout = 30
                sslOptions.isUseAlpn = false
                webSocketCompressionLevel = 6
                webSocketSubProtocols = arrayListOf("wss", "ws")

                if (isSsl) require(loadCert(this, sslPath, password)) {
                    IllegalArgumentException("Could not find a valid SSL certificate in the path $sslPath")
                }
            }
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

    class WebHookConfigBuilder {
        private var _sslPath: String = "./"
        private var _password: String = ""
        private var _isSSL: Boolean = true
        private var _options: HttpServerOptions? = null
        private var _port: Int = 443
        private var _host: String = "0.0.0.0"
        private var _enableWebSocketForwarding: Boolean = false
        private var _webSocketPath: String = "/websocket"
        private var _enableWebSocketForwardingLoginVerify: Boolean = true
        private var _enableWebSocketForwardingIntentsVerify: Boolean = false

        /**
         * SSL 文件路径
         */
        fun sslPath(value: String): WebHookConfigBuilder {
            val file = File(value)
            if (!file.exists() || !file.isDirectory) {
                throw IllegalArgumentException("SSL directory not found or is not a directory")
            }
            _sslPath = value
            return this
        }

        /**
         * SSL 密码，如果没有则留空
         */
        fun password(value: String): WebHookConfigBuilder {
            _password = value
            return this
        }

        /**
         * 是否启用 SSL
         */
        fun isSSL(value: Boolean): WebHookConfigBuilder {
            _isSSL = value
            return this
        }

        /**
         * HttpServerOptions 实例。
         * 如果无法加载证书，请自行加载，设置 HttpServerOptions.keyCertOptions
         */
        fun options(value: HttpServerOptions): WebHookConfigBuilder {
            _options = value
            return this
        }

        /**
         * WebHook Server 端口，如果为 0 则是随机端口
         */
        fun port(value: Int): WebHookConfigBuilder {
            if (value < 0) throw IllegalArgumentException("Port must be greater than 0")
            if (value > 65535) throw IllegalArgumentException("Port must be less than 65535")
            _port = value
            return this
        }

        /**
         * WebHook Server 监听地址
         */
        fun host(value: String): WebHookConfigBuilder {
            _host = value
            return this
        }

        /**
         * 是否启用 WebSocket 转发
         */
        fun enableWebSocketForwarding(value: Boolean): WebHookConfigBuilder {
            _enableWebSocketForwarding = value
            return this
        }

        /**
         * WebSocket 暴露转发路径
         */
        fun webSocketPath(value: String): WebHookConfigBuilder {
            _webSocketPath = value
            return this
        }

        /**
         * 是否启用 WebSocket 转发登录验证
         */
        fun enableWebSocketForwardingLoginVerify(value: Boolean): WebHookConfigBuilder {
            _enableWebSocketForwardingLoginVerify = value
            return this
        }

        /**
         * 是否启用 WebSocket 转发 intents 验证
         * 开启后会根据 WS 客户端的 Intents 来转发被订阅的事件
         */
        fun enableWebSocketForwardingIntentsVerify(value: Boolean): WebHookConfigBuilder {
            _enableWebSocketForwardingIntentsVerify = value
            return this
        }

        /**
         * 构建并返回 WebHookConfig 实例
         */
        fun build(): WebHookConfig = WebHookConfig(
            sslPath = _sslPath,
            password = _password,
            isSSL = _isSSL,
            options = createHttpServerOptions(_sslPath, _isSSL, _password),
            port = _port,
            host = _host,
            enableWebSocketForwarding = _enableWebSocketForwarding,
            webSocketPath = _webSocketPath,
            enableWebSocketForwardingLoginVerify = _enableWebSocketForwardingLoginVerify,
            enableWebSocketForwardingIntentsVerify = _enableWebSocketForwardingIntentsVerify
        )
    }
}
