package io.github.zimoyin.ra3

import io.github.zimoyin.ra3.config.BotConfig
import io.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import io.github.zimoyin.qqbot.bot.Bot
import io.github.zimoyin.qqbot.net.http.TencentOpenApiHttpClient
import io.github.zimoyin.qqbot.net.webhook.WebHookConfig
import io.github.zimoyin.ra3.expand.registerSingletonBean
import jakarta.annotation.PostConstruct
import org.mybatis.spring.annotation.MapperScan
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.ApplicationContext

/**
 *
 * @author : zimo
 * @date : 2025/01/03
 */
@SpringBootApplication
@EnableCaching
@MapperScan(basePackages = ["io.github.zimoyin.ra3.mapper"])
class ApplicationStart(
    val config: BotConfig,
    val applicationContext: ApplicationContext,
) {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<ApplicationStart>(*args)
        }
    }

    private val logger = LoggerFactory.getLogger(ApplicationStart::class.java)

    val bot: Bot by lazy {
        kotlin.runCatching {
            TencentOpenApiHttpClient.isSandBox = config.isSandBox
        }.onFailure {
            logger.warn("已经设置了是否使用沙盒环境，请不要再此设置", it)
        }
        Bot.createBot(config.token.toToken(), config.websocket.intents).apply {
            logger.info("机器人创建成功: $this")
        }
    }

    private val webhookConfig: WebHookConfig by lazy {
        WebHookConfig.WebHookConfigBuilder()
            .host(config.webhook.host)
            .port(config.webhook.port)
            .sslPath(config.webhook.sslPath)
            .isSSL(config.webhook.ssl)
            .enableWebSocketForwarding(config.webhook.enableWebSocketForwarding)
            .enableWebSocketForwardingLoginVerify(config.webhook.enableWebSocketForwardingLoginVerify)
            .webSocketPath(config.webhook.webSocketPath)
            .password(config.webhook.password)
            .build()
    }


    @PostConstruct
    fun registerVertx() {
        GLOBAL_VERTX_INSTANCE.registerSingletonBean(applicationContext, "vertx")
    }

    @PostConstruct
    fun onStartWebHook() {
        if (config.webhook.enable.not()) return
        logger.info("WebHook启动中...")
        bot.start(webhookConfig).onSuccess {
            it.registerSingletonBean(applicationContext, "webServer")
            it.router.registerSingletonBean(applicationContext, "router")
            logger.info("WebHook启动成功，监听地址: ${it.port}")
        }.onFailure {
            logger.error("WebHook启动失败", it)
            GLOBAL_VERTX_INSTANCE.close()
        }
    }

    @PostConstruct
    fun onStartWebSocket() {
        if (config.websocket.enable.not()) return
        logger.info("WebSocket启动中...")
        bot.login(config.websocket.isVerifyHost).onSuccess {
            logger.info("Bot 启动完成")
        }.onFailure {
            logger.error("Bot 启动失败", it)
            GLOBAL_VERTX_INSTANCE.close()
        }
    }
}


