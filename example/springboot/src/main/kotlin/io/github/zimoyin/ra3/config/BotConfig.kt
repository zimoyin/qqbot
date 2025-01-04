package io.github.zimoyin.ra3.config

import io.github.zimoyin.qqbot.net.Intents
import io.github.zimoyin.qqbot.net.Token
import io.github.zimoyin.qqbot.net.http.TencentOpenApiHttpClient
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 *
 * @author : zimo
 * @date : 2025/01/03
 */
@Configuration
@Validated
@ConfigurationProperties(prefix = "qqbot")
@Component
data class BotConfig(
    var token: TokenConfig = TokenConfig(),
    var webhook: WebhookConfig = WebhookConfig(),
    var websocket: WebSocket = WebSocket(),
    var isSandBox: Boolean = true
) {
    data class TokenConfig(
        var appid: String = "", var token: String = "", var secret: String = ""
    ) {
        fun toToken(): Token {
            return Token.create(appID = appid, token = token, appSecret = secret)
        }
    }

    data class WebSocket(
        var enable: Boolean = false,
        var host: String = TencentOpenApiHttpClient.host,
        var webSocketForwardingAddress: String? = TencentOpenApiHttpClient.webSocketForwardingAddress,
        var isUseCustomHost: Boolean = true,
        var isVerifyHost: Boolean = true,
        var intents : Intents.Presets = Intents.Presets.PRIVATE_GROUP_INTENTS
    )

    data class WebhookConfig(
        var enable: Boolean = false,
        var port: Int = 443,
        var sslPath: String = "./ssl",
        var isSSL: Boolean = true,
        var host: String = "0.0.0.0",
        var enableWebSocketForwarding: Boolean = true,
        var enableWebSocketForwardingLoginVerify: Boolean = true,
        var webSocketPath: String = "/websocket",
        var password: String = ""
    )
}

