package io.github.zimoyin.qqbot

import io.github.zimoyin.qqbot.bot.Bot
import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import io.github.zimoyin.qqbot.net.http.TencentOpenApiHttpClient
import io.github.zimoyin.qqbot.net.webhook.WebHookConfig
import java.io.File
import java.io.InputStream
import java.util.*
import java.util.logging.*
import kotlin.system.exitProcess


/**
 *
 * @author : zimo
 * @date : 2024/12/21
 */
fun main() {
    println("开始启动")
    if (!LocalLogger.isSlf4jImplClassExists()) {
        println("---------------------------------------------------------------------------------")
        println(">>> 项目中没有存在SLF4J实现类,将使用JUL日志 <<<")
        println("如果想要使用 SLF4J实现类,请自行添加依赖")
        println("")
        println(">>> 示例 log4j2: <<<")
        println("1. 下载JAR: https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-api/2.24.3/log4j-api-2.24.3.jar")
        println("2. 下载JAR: https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-core/2.24.3/log4j-core-2.24.3.jar")
        println("3. 配置 log4j2 的 XML 文件：log4j2.xml")
        println("4. 配置启动脚本: java -Dlog4j.configurationFile=log4j2.xml -cp \"log4j-api-2.24.3.jar;log4j-core-2.24.3.jar;qqbot-1.2.8-fat.jar\" io.github.zimoyin.qqbot.MainKt ")
        println("注意在 log4j2 中配置日志编码，防止控制台输出与日志输出编码不一致")
        println("---------------------------------------------------------------------------------")
    }
    println("")
    SystemLogger.debug("已经启用DEBUG级别日志")
    // 从配置文件中读取值
    val config = loadConfig()
    val appid = config.getProperty("appid") ?: throw IllegalArgumentException("appid不能为空")
    val token = config.getProperty("token") ?: ""
    val secret = config.getProperty("secret") ?: ""
    val sslPath = config.getProperty("sslPath") ?: "./"
    val isSsl = config.getProperty("isSsl")?.toBoolean() ?: true
    val enableWebSocketForwarding = config.getProperty("enableWebSocketForwarding")?.toBoolean() ?: true
    val port = config.getProperty("port")?.toInt() ?: 443
    val password = config.getProperty("password") ?: ""
    val webSocketPath = config.getProperty("webSocketPath") ?: "/websocket"
    val host = config.getProperty("host") ?: "0.0.0.0"
    val enableWebSocketForwardingLoginVerify =
        config.getProperty("enableWebSocketForwardingLoginVerify")?.toBoolean() ?: true
    val isSandBox = config.getProperty("isSandBox")?.toBoolean() ?: true
    val debugLog = config.getProperty("debugLog")?.toBoolean() ?: false
    val mataDataDebugLog = config.getProperty("mataDataDebugLog")?.toBoolean() ?: false
    val loggerLevel = config.getProperty("loggerLevel") ?: "INFO"

    LocalLogger.changeJULogging(Level.parse(loggerLevel))

    TencentOpenApiHttpClient.isSandBox = isSandBox
    val webConfig = WebHookConfig(
        sslPath = sslPath,
        password = password,
        isSSL = isSsl,
        port = port,
        host = host,
        enableWebSocketForwarding = enableWebSocketForwarding,
        webSocketPath = webSocketPath,
        enableWebSocketForwardingLoginVerify = enableWebSocketForwardingLoginVerify
    )

    GlobalEventBus.onEvent<Event> {
        println("全局事件监听: ${it.metadataType}")
    }

    Bot.createBot(appid, token, secret).apply {
        context["PAYLOAD_CMD_HANDLER_DEBUG_LOG"] = debugLog
        context["PAYLOAD_CMD_HANDLER_DEBUG_MATA_DATA_LOG"] = mataDataDebugLog
        if (debugLog || mataDataDebugLog) {
            SystemLogger.info("已经开启 HttpWebHook DEBUG 日志")
        }
        start(webConfig).onSuccess {
            SystemLogger.info("启动成功: $host:${it.webHttpServer.actualPort()}")
        }.onFailure {
            println("启动失败: ${it.message}")
            it.printStackTrace()
            exitProcess(1)
        }
    }
}

private fun loadConfig(): Properties {
    val file = File("./application.properties")
    if (file.exists().not()) {
        file.writeText(
            """
        appid=
        token=
        secret=
        sslPath=./
        isSsl=true
        enableWebSocketForwarding=true
        port=443
        password=
        webSocketPath=/websocket
        host=0.0.0.0
        enableWebSocketForwardingLoginVerify=true
        isSandBox=true
        debugLog=false
        mataDataDebugLog=false
        loggerLevel=INFO # ALL，SEVERE，WARNING，INFO，CONFIG，FINE，FINER，FINEST，OFF
    """.trimIndent()
        )
        SystemLogger.error("配置文件application.properties不存在,已创建默认配置文件,请修改后再次运行程序")
        exitProcess(1)
    }
    val properties = Properties()
    val inputStream: InputStream = file.inputStream()
    properties.load(inputStream)
    return properties
}
