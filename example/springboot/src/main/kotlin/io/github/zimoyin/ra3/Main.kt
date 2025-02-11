package io.github.zimoyin.ra3

import io.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import io.github.zimoyin.qqbot.bot.Bot
import io.github.zimoyin.qqbot.command.SimpleCommandRegistrationCenter
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import io.github.zimoyin.qqbot.net.http.TencentOpenApiHttpClient
import io.github.zimoyin.qqbot.net.webhook.WebHookConfig
import io.github.zimoyin.qqbot.utils.MediaManager
import io.github.zimoyin.ra3.config.BotConfig
import io.github.zimoyin.ra3.expand.registerSingletonBean
import io.vertx.core.eventbus.MessageConsumer
import jakarta.annotation.PostConstruct
import org.mybatis.spring.annotation.MapperScan
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationFailedEvent
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.EventListener


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
            TencentOpenApiHttpClient.isSandBox = config.sandBox
            logger.info("设置了是否使用沙盒环境: ${config.sandBox}")
        }.onFailure {
            logger.warn("已经设置了是否使用沙盒环境，框架无法再次选择")
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

    @EventListener(ContextClosedEvent::class)
    fun handleContextClosed(event: ContextClosedEvent) {
        runCatch { bot.close() }
        runCatch {
            val vertx = bot.config.vertx
            vertx.deploymentIDs().forEach { id ->
                runCatch { vertx.undeploy(id).onFailure { logger.error("undeploy失败", it) } }
            }
        }
        runCatch { unregisters(GlobalEventBus.consumers) }
        runCatch { unregisters(bot.config.consumers) }
        runCatch { MediaManager.instance.clear() }
        runCatch {
            for (commandObject in SimpleCommandRegistrationCenter.getCommandList().toMutableList()) {
                SimpleCommandRegistrationCenter.unregister(commandObject)
            }
        }
    }

    fun runCatch(callback: () -> Unit) {
        kotlin.runCatching {
            callback()
        }.onFailure {
            logger.error("捕获到异常", it)
        }
    }

    private fun unregisters(consumers0: HashSet<MessageConsumer<*>>) {
        val consumers = consumers0.toMutableList()
        val toRemove = mutableListOf<MessageConsumer<*>>()
        for (consumer in consumers) {
            consumer.unregister().onSuccess {
                toRemove.add(consumer)
            }
        }
        consumers0.removeAll(toRemove.toSet())
    }

    @EventListener(ApplicationFailedEvent::class)
    fun handleApplicationFailedEvent(event: ApplicationFailedEvent) {
        runCatch { bot.close() }
        runCatch { GLOBAL_VERTX_INSTANCE.close() }
    }

    @PostConstruct
    fun close() {
        Runtime.getRuntime().addShutdownHook(Thread {
            runCatch { GLOBAL_VERTX_INSTANCE.close() }
        })
    }
}


