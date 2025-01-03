package io.github.zimoyin

import io.github.zimoyin.config.BotConfig
import io.github.zimoyin.qqbot.bot.Bot
import io.github.zimoyin.qqbot.net.webhook.WebHookConfig
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.event.EventListener

/**
 *
 * @author : zimo
 * @date : 2025/01/03
 */
@SpringBootApplication
class ApplicationStart(val config: BotConfig) {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<ApplicationStart>(*args)
        }
    }

    private val logger = LoggerFactory.getLogger(ApplicationStart::class.java)

    val bot: Bot by lazy {
        TencentOpenApiHttpClient.isSandBox = config.isSandBox
        Bot.createBot(config.token.toToken(), config.websocket.intents).apply {
            logger.info("机器人创建成功: $this")
        }
    }

    private val webhookConfig: WebHookConfig by lazy {
        WebHookConfig.WebHookConfigBuilder()
            .host(config.webhook.host)
            .port(config.webhook.port)
            .sslPath(config.webhook.sslPath)
            .isSSL(config.webhook.isSSL)
            .enableWebSocketForwarding(config.webhook.enableWebSocketForwarding)
            .enableWebSocketForwardingLoginVerify(config.webhook.enableWebSocketForwardingLoginVerify)
            .webSocketPath(config.webhook.webSocketPath)
            .password(config.webhook.password)
            .build()
    }

    @EventListener(ApplicationReadyEvent::class)
    fun onStartWebHook(event: ApplicationReadyEvent) {
        if (config.webhook.enable.not()) return
        logger.info("WebHook启动中...")
        bot.start(webhookConfig).onSuccess {
            logger.info("WebHook启动成功，监听地址: ${it.port}")
        }.onFailure {
            logger.error("WebHook启动失败", it)
        }
    }

    @EventListener(ApplicationReadyEvent::class)
    fun onStartWebSocket(event: ApplicationReadyEvent) {
        if (config.websocket.enable.not()) return
        logger.info("WebSocket启动中...")
        bot.login(config.websocket.isVerifyHost).onSuccess {
            logger.info("Bot 启动完成")
        }.onFailure{
            logger.error("Bot 启动失败", it)
        }
    }
}


