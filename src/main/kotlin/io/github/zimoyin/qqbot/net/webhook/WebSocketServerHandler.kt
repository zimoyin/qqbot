package io.github.zimoyin.qqbot.net.webhook

import com.fasterxml.jackson.core.JsonParseException
import io.github.zimoyin.qqbot.LocalLogger
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.utils.ex.toJAny
import io.github.zimoyin.qqbot.utils.ex.toJsonObject
import io.vertx.core.http.HttpServer
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.http.ServerWebSocketHandshake
import io.vertx.core.json.DecodeException
import io.vertx.kotlin.core.json.jsonObjectOf
import java.util.*
import kotlin.math.log

/**
 *
 * @author : zimo
 * @date : 2024/12/26
 */
class WebSocketServerHandler(private val server: WebHookHttpServer) {

    private val logger = LocalLogger(this::class.java)
    private val webHookConfig = server.webHookConfig
    private val wsList = server.wsList
    private var isDebug = false
    private var isMataDebug = false

    init {
        val bot = server.bot
        isDebug = bot.context["PAYLOAD_CMD_HANDLER_DEBUG_LOG"] ?: false
        isMataDebug = bot.context["PAYLOAD_CMD_HANDLER_DEBUG_MATA_DATA_LOG"] ?: false
    }

    /**
     * 添加WebSocket转发, 让该程序作为WebSocket服务器，可以允许客户端进行连接
     */
    fun addWebSocketForwarding(httpServer: HttpServer): HttpServer {
        if (!webHookConfig.enableWebSocketForwarding) return httpServer
        return httpServer.webSocketHandler { ws ->
            if (ws.path() != webHookConfig.webSocketPath) {
                ws.reject()
                return@webSocketHandler
            }
            val id = UUID.randomUUID()
            logger.info("[WebSocketServer] 新连接: $id")
            var hid: Long = 1
            wsList.add(ws)

            opStart(ws)
            ws.textMessageHandler { text ->
                runCatching {
                    if (isMataDebug) logger.debug("WebSocketServer 收到消息: $text")
                    val payload = text.toJsonObject().mapTo(Payload::class.java)
                    when (payload.opcode) {
                        2 -> opcode2(ws, hid, id)
                        1 -> opcode1(ws, hid)
                        6 -> opcode6(ws, id)
                        else -> {
                            logger.warn("[WebSocketServer] 不支持的opcode: ${payload.opcode}")
                        }
                    }
                    hid++
                }.onFailure {
                    val op502 = Payload(
                        opcode = 502,
                        eventType = "ERROR",
                        hid = hid,
                        eventID = id.toString(),
                        eventContent = jsonObjectOf("code" to 502, "message" to "接受到错误信息: ${text}").toJAny()
                    )
                    if (it is JsonParseException) {
                        logger.warn("WebSocketServer] 接受到错误信息: ${text}")
                        ws.writeTextMessage(op502.toJsonString())
                        return@onFailure
                    }
                    if (it is DecodeException) {
                        logger.warn("WebSocketServer] 接受到错误信息: ${text}")
                        ws.writeTextMessage(op502.toJsonString())
                        return@onFailure
                    }
                    logger.warn("WebSocketServer] text: ${text}", it)
                }
            }

            ws.closeHandler {
                logger.info("[WebSocketServer] 断开连接: $id")
                wsList.remove(ws)
            }

            ws.exceptionHandler {
                logger.warn("[WebSocketServer] 错误: ${it.message}", it)
            }
        }
    }

    private fun opStart(ws: ServerWebSocket) {
        val start = Payload(
            opcode = 10,
            eventContent = jsonObjectOf("heartbeat_interval" to 45000).toJAny()
        )
        ws.writeTextMessage(start.toJsonString())
    }

    private fun opcode6(ws: ServerWebSocket, id: UUID) {
        val o6 = Payload(
            opcode = 0,
            eventType = "RESUMED",
            eventContent = "".toJAny()
        )

        ws.writeTextMessage(o6.toJsonString())
    }

    private fun opcode1(ws: ServerWebSocket, hid: Long) {
        val o1 = Payload(
            opcode = 11,
            hid = hid,
        )
        ws.writeTextMessage(o1.toJsonString())
    }

    private fun opcode2(ws: ServerWebSocket, hid: Long, id: UUID) {
        val o2 = Payload(
            opcode = 0,
            hid = hid,
            eventType = "READY",
            eventContent = """
                 {
                    "version": 1,
                    "session_id": "$id",
                    "user": {
                      "id": "",
                      "username": "",
                      "bot": true
                    },
                    "shard": [0, 0]
                  }
            """.trimIndent().toJsonObject().toJAny()
        )
        ws.writeTextMessage(o2.toJsonString())
    }
}
