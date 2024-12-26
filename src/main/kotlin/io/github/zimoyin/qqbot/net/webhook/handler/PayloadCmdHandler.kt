package io.github.zimoyin.qqbot.net.webhook.handler

import io.github.zimoyin.qqbot.LocalLogger
import io.github.zimoyin.qqbot.bot.Bot
import io.github.zimoyin.qqbot.event.events.platform.bot.BotOnlineEvent
import io.github.zimoyin.qqbot.event.supporter.BotEventBus
import io.github.zimoyin.qqbot.event.supporter.EventMapping
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.github.zimoyin.qqbot.net.http.api.accessToken
import io.github.zimoyin.qqbot.utils.ex.toJsonObject
import io.vertx.core.MultiMap
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServerResponse


/**
 *
 * @author : zimo
 * @date : 2023/12/07
 */
class PayloadCmdHandler(
    private val bot: Bot,
    private val vertx: Vertx,
) {

    private var debugMataData = false
    private var debugLog = false
    private var debugHeartbeat = false

    private val logger = LocalLogger(PayloadCmdHandler::class.java)
    private var eventBus: BotEventBus
    private var timerId: Long = -1

    init {
        EventMapping
        debugLog = bot.context.getBoolean("PAYLOAD_CMD_HANDLER_DEBUG_LOG") ?: debugLog
        debugMataData = bot.context.getBoolean("PAYLOAD_CMD_HANDLER_DEBUG_MATA_DATA_LOG") ?: debugMataData
        debugHeartbeat = bot.context.getBoolean("PAYLOAD_CMD_HANDLER_DEBUG_HEART_BEAT") ?: debugHeartbeat
        eventBus = bot.config.botEventBus
        if (debugLog) logger.debug("Token version: ${bot.config.token.version}")
        if (bot.config.token.version >= 2) updateToken()
    }

    private fun updateToken() {
        val token = bot.config.token
        if (debugLog) logger.debug("更新token中...")
        HttpAPIClient.accessToken(token).onSuccess {
            timerId = vertx.setTimer((token.expiresIn.toLong() - 60) * 1000) {
                updateToken()
            }
            if (debugLog) logger.debug("更新token成功: ${token.expiresIn} s")
        }.onFailure {
            logger.warn("更新token失败: ${it}")
            timerId = vertx.setTimer(3 * 1000) {
                updateToken()
            }
        }
    }

    fun handle(headers: MultiMap, payload0: Payload, response: HttpServerResponse) {
        payload0.appID = bot.config.token.appID
        handle(payload0, headers, response)
    }

    private fun handle(payload: Payload, headers: MultiMap, response: HttpServerResponse) {
        if (bot.config.token.version >= 2 && timerId > -1) updateToken()
        if (debugMataData) logger.debug("payload: ${payload.toJsonString()}")
        when (payload.opcode) {
            0 -> opcode0(payload, response) //服务端进行消息推送
            13 -> opcode13(payload, headers, response) // 参数错误比如要求的权限不合适
            else -> logger.error("WebHook receive(unknown) : ${payload}")
        }
    }

    private fun opcode0(payload: Payload, response: HttpServerResponse) {
        payload.eventType?.apply {// 获取元事件类型
            if (debugLog) logger.debug("receive(0) 元事件类型: $this")
            EventMapping.get(this)?.apply {// 获取注册的元事件
                eventHandler.getDeclaredConstructor().newInstance().apply { // 获取该事件类型的处理器
                    if (debugLog) logger.debug(
                        "receive(0) 事件处理器: ${this::class.java.typeName}",
                    )
                    try {
                        eventBus.broadcastAuto(handle(payload)) //广播事件
                    } catch (e: Exception) {
                        logger.error("广播事件失败: ${payload.eventType} -> ${payload.metadata}", e)
                    }
                }
            } ?: logger.warn("未注册的事件类型: ${payload.eventType} -> ${payload.metadata}")
        } ?: logger.debug("服务器推送的消息为空(ws send(0)): ${payload.metadata}")
        response.write(Payload(opcode = 12).toJsonString())
    }

    private fun opcode13(payload: Payload, headers: MultiMap, response: HttpServerResponse) {
        val d = payload.eventContent?.toJsonObject() ?: throw RuntimeException("WebHook receive(13) : event is null")
        if (debugLog) logger.debug("服务器访问 op: ${payload.opcode}")
        response.putHeader("X-Bot-Appid", bot.config.token.appID)
        val json = d.apply {
            put(
                "signature",
                Verify().verify(bot, d.getString("event_ts"), d.getString("plain_token"))
            )
        }
        eventBus.broadcastAuto(
            BotOnlineEvent(
                metadata = payload.metadata,
                botInfo = bot.botInfo
            )
        )
        response.write(json.encode())
    }

    fun close() {
        if (timerId >= 0) vertx.cancelTimer(timerId)
        timerId = -1
    }
}
