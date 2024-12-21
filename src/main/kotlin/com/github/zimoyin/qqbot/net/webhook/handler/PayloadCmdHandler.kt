package com.github.zimoyin.qqbot.net.webhook.handler

import com.github.zimoyin.qqbot.LocalLogger
import com.github.zimoyin.qqbot.bot.Bot
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.platform.bot.BotHelloEvent
import com.github.zimoyin.qqbot.event.events.platform.bot.BotOnlineEvent
import com.github.zimoyin.qqbot.event.events.platform.bot.BotReconnectNotificationEvent
import com.github.zimoyin.qqbot.event.supporter.BotEventBus
import com.github.zimoyin.qqbot.event.supporter.EventMapping
import com.github.zimoyin.qqbot.exception.WebSocketReconnectException
import com.github.zimoyin.qqbot.net.bean.Payload
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.net.http.api.accessTokenUpdateAsync
import com.github.zimoyin.qqbot.utils.JSON
import com.github.zimoyin.qqbot.utils.ex.mapTo
import com.github.zimoyin.qqbot.utils.ex.toJAny
import com.github.zimoyin.qqbot.utils.ex.toJsonObject
import com.github.zimoyin.qqbot.utils.ex.writeToText
import io.vertx.core.MultiMap
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.http.WebSocket
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.impl.jose.JWS.verifySignature
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory


/**
 *
 * @author : zimo
 * @date : 2023/12/07
 */
class PayloadCmdHandler(
    private val bot: Bot,
) {

    private var debugMataData = false
    private var debugLog = false
    private var debugHeartbeat = false

    private val logger = LocalLogger(PayloadCmdHandler::class.java)
    private lateinit var eventBus: BotEventBus

    init {
        EventMapping
        debugLog = bot.context.getBoolean("PAYLOAD_CMD_HANDLER_DEBUG_LOG") ?: debugLog
        debugMataData = bot.context.getBoolean("PAYLOAD_CMD_HANDLER_DEBUG_MATA_DATA_LOG") ?: debugMataData
        debugHeartbeat = bot.context.getBoolean("PAYLOAD_CMD_HANDLER_DEBUG_HEART_BEAT") ?: debugHeartbeat
        eventBus = bot.config.botEventBus
    }

    fun handle(headers: MultiMap, body: Buffer, response: HttpServerResponse) {

        kotlin.runCatching {
            val payload0 = body.mapTo(Payload::class.java)
            payload0.metadata = body.writeToText().let {
                return@let try {
                    JSON.toJsonObject(it).encode()
                } catch (e: Exception) {
                    it
                }
            }
            payload0.appID = bot.config.token.appID

            handle(payload0, headers, response)
        }.onFailure {
            logger.error("PayloadCmdHandler 无法解析的信息: ${body.writeToText()}", it)
        }
    }

    private fun handle(payload: Payload, headers: MultiMap, response: HttpServerResponse) {
        when (payload.opcode) {
            0 -> opcode0(payload, response) //服务端进行消息推送
            13 -> opcode13(payload, headers, response) // 参数错误比如要求的权限不合适
            else -> logger.error("WebHook receive(unknown) : ${payload}", )
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
        } ?: logger.debug("服务器推送的消息为空(ws send(0)): ${payload.metadata}", )
        response.write(Payload(opcode = 12).toJsonString())
    }

    private fun opcode13(payload: Payload, headers: MultiMap, response: HttpServerResponse) {
        val d = payload.eventContent?.toJsonObject() ?: throw RuntimeException("WebHook receive(13) : event is null")
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
}
