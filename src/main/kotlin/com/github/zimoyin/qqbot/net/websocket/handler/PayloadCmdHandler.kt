package com.github.zimoyin.qqbot.net.websocket.handler

import com.github.zimoyin.qqbot.bot.Bot
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.platform.bot.BotHelloEvent
import com.github.zimoyin.qqbot.event.events.platform.bot.BotOnlineEvent
import com.github.zimoyin.qqbot.event.events.platform.bot.BotReconnectNotificationEvent
import com.github.zimoyin.qqbot.event.supporter.BotEventBus
import com.github.zimoyin.qqbot.event.supporter.EventMapping
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.net.http.api.accessTokenUpdateAsync
import com.github.zimoyin.qqbot.net.bean.Payload
import com.github.zimoyin.qqbot.utils.JSON
import com.github.zimoyin.qqbot.utils.ex.mapTo
import com.github.zimoyin.qqbot.utils.ex.toJAny
import com.github.zimoyin.qqbot.utils.ex.writeToText
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpClosedException
import io.vertx.core.http.WebSocket
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 *
 * @author : zimo
 * @date : 2023/12/07
 */
class PayloadCmdHandler(private val bot: Bot) {


  private val logger: Logger by lazy { LoggerFactory.getLogger(PayloadCmdHandler::class.java) }
  private val debugMataData = false
  private val debugLog = false
  private val debugHeartbeat = false

  private var vertx: Vertx
  private var ws: WebSocket
  private var promise: Promise<WebSocket>
  private var eventBus: BotEventBus


  /**
   * 当前链接的SessionID
   */
  private var sessionID: String? = null

  /**
   * 当前会话最新ID，通常用于重连服务器后补发该ID之后的事件
   */
  private var id: Long? = null

  /**
   * 是否已经开启的心跳
   */
  private var openHeartbeat = false

  /**
   * 心跳计数器的ID
   */
  private var heartbeatId: Long = 0
  private val headerCycle: Long

  private var reconnect = false
  private var newconnecting = false

  init {
    EventMapping
    headerCycle = bot.context.getValue<Long>("internal.headerCycle")
    ws = bot.context.getValue<WebSocket>("ws")
    vertx = bot.context.getValue<Vertx>("vertx")
    promise = bot.context.getValue<Promise<WebSocket>>("internal.promise")
    eventBus = bot.config.botEventBus
    if (bot.context.getString("SESSION_ID") != null) {
      sessionID = bot.context.getString("SESSION_ID")
    }
    ws.closeHandler {
      close()
    }
//        logger.info("PayloadCmdHandler[临时追踪ID(${this.uid()})] 处理器创建完成，绑定Bot AppID: ${bot.config.token.appID}")
  }


  fun handle(buffer: Buffer) {
    if (ws.isClosed) {
      ws = bot.context.getValue<WebSocket>("ws")
      vertx = bot.context.getValue<Vertx>("vertx")
      promise = bot.context.getValue<Promise<WebSocket>>("internal.promise")
      eventBus = bot.config.botEventBus
    }
    kotlin.runCatching {
      val payload0 = buffer.mapTo(Payload::class.java)
      payload0.metadata = buffer.writeToText().let {
        return@let try {
          JSON.toJsonObject(it).encode()
        } catch (e: Exception) {
          it
        }
      }
      payload0.appID = bot.config.token.appID

      handle(payload0)
    }.onFailure {
      logger.error("PayloadCmdHandler[${this.uid()}] 无法解析的信息: ${buffer.writeToText()}", it)
    }

    if (reconnect) {
      reconnect = false
      throw HttpClosedException("The server requires the client to reconnect")
    }
  }

  private fun handle(payload: Payload) {
    if (debugMataData) logger.debug("ws receive: {}", payload)
    if (payload.hid != null) id = payload.hid
//      id = id?.plus(1) ?: 0
    try {
      when (payload.opcode) {
        0 -> opcode0(payload) //服务端进行消息推送
        10 -> opcode10() // 当客户端与网关建立 ws 连接之后，网关下发的第一条消息 : Hello
        11 -> opcode11() // 服务器响应心跳
        7 -> opcode7(payload) // 服务器通知客户端重新连接
        9 -> opcode9(payload) // 参数错误比如要求的权限不合适
        else -> logger.warn("WebSocket[${ws.hashCode()}] receive(unknown) : {}", payload)
      }
    } catch (e: Exception) {
      logger.error(
        "PayloadCmdHandler[${this.uid()}] 解析失败 opcode:${payload.opcode}: ${payload.metadata}",
        e
      )
    }
  }

  private fun opcode9(payload: Payload) {
    if (newconnecting) logger.error("WebSocket[${ws.hashCode()}] Intents是不合法的，你没有这个Intent的权限。请检查该Bot[${payload.appID}]的权限")
    else  logger.error("WebSocket[${ws.hashCode()}] 无法重启这个会话，请建立新的会话。请检查该Bot[${payload.appID}]的会话并重启")
  }

  private fun opcode7(payload: Payload) {
    eventBus.broadcastAuto(
      BotReconnectNotificationEvent(
        botInfo = BotInfo.create(payload.appID!!),
      )
    )
    reconnect = true
    logger.info("WebSocket[${ws.hashCode()}] 服务器通知客户端重新连接")
  }

  /**
   * 服务端进行消息推送
   */
  private fun opcode0(payload: Payload) {
    openHeartbeat()
    if (debugLog) logger.debug("WebSocket[${ws.hashCode()}] receive(0): {}", payload)
    if (payload.eventType == "READY") {
      promise.complete(ws)
      sessionID = JSON.toJsonObject(payload.metadata).getJsonObject("d").let {
        JSON.toJsonObject(it).getString("session_id")
      }
      //平台事件机器人上线事件，非平台事件 见 RESUMED
      eventBus.broadcastAuto(
        BotOnlineEvent(
          metadata = payload.metadata,
          botInfo = BotInfo.create(payload.appID!!)
        )
      )
      logger.info("WebSocket[${ws.hashCode()}] 鉴权成功:建立长连接 [SESSION_ID: $sessionID]")
    } else if (payload.eventType == "RESUMED") {
      //平台事件机器人上线事件，非平台事件 见 RESUMED
      eventBus.broadcastAuto(
        BotOnlineEvent(
          metadata = payload.metadata,
          botInfo = BotInfo.create(payload.appID!!)
        )
      )
      logger.info("WebSocket[${ws.hashCode()}] 鉴权成功:已完成重连 [SESSION_ID: $sessionID]")
    }

    //该事件广播实现只广播具有元事件类型的服务器下发的信息，对于没有的需要抛出异常活着使用其他的广播
    broadcast(payload)
  }

  private fun broadcast(payload: Payload) {
    payload.eventType?.apply {// 获取元事件类型
      EventMapping.get(this)?.apply {// 获取注册的元事件
        eventHandler.getDeclaredConstructor().newInstance().apply { // 获取该事件类型的处理器
          try {
            eventBus.broadcastAuto(handle(payload)) //广播事件
          } catch (e: Exception) {
            logger.error("广播事件失败: ${payload.eventType} -> ${payload.metadata}", e)
          }
        }
      } ?: logger.warn("未注册的事件类型: ${payload.eventType} -> ${payload.metadata}")
    } ?: logger.debug("WebSocket[${ws.hashCode()}] 服务器推送的消息为空(ws send(0)): {}", payload.metadata)
  }

  /**
   * 当客户端与网关建立 ws 连接之后，网关下发的第一条消息
   * 客户端接收到后根据情况选择重连还是建立新连接
   */
  private fun opcode10() {
    eventBus.broadcastAuto(BotHelloEvent(bot.botInfo))
    if (debugLog) logger.debug("WebSocket[${ws.hashCode()}] receive(10): Hello ")
    if (sessionID == null) {
      connect()
    } else {
      reconnect()
    }
  }

  private fun reconnect() {
    newconnecting = false
    bot.context.set("newconnecting", newconnecting)
    val config = bot.config

    val message = JsonObject().apply {
      put("token", config.token.getHeaders().get("Authorization"))
      put("session_id", sessionID)
      put("seq", id ?: 0)
    }

    if (debugLog) logger.debug("WebSocket[${ws.hashCode()}] send(6): Resume")
    send(Payload(6, message.toJAny()))
  }

  private fun connect() {
    newconnecting = true
    bot.context.set("newconnecting", newconnecting)
    val config = bot.config
    val shards = bot.context.getValue<Int>("shards")
    if (shards < config.shards.total) logger.warn("WebSocket[${ws.hashCode()}] 切片总量大于官方推荐切片总量: ${config.shards.total} > $shards")
    val message = JsonObject().apply {
      put("token", config.token.getHeaders().get("Authorization"))
      put("intents", config.intents)
      put("shard", JsonArray().add(config.shards.index).add(config.shards.total))
      put("properties", "")//官方无规定，这里留空
    }

    if (debugLog) logger.debug("WebSocket[${ws.hashCode()}] send(2): Identify")
    send(Payload(2, message.toJAny()))
  }

  /**
   * 当发送心跳成功之后，就会收到该消息
   */
  private fun opcode11() {
    if (debugLog && debugHeartbeat) logger.debug("WebSocket[${ws.hashCode()}] receive(11): Heartbeat ACK")
  }

  private fun openHeartbeat() {
    val pch = this
    if (!openHeartbeat) {
      heartbeatId = vertx.setPeriodic(headerCycle) {
        if (ws.isClosed) {
          close()
          return@setPeriodic
        }
        if (bot.config.token.version > 1) HttpAPIClient.accessTokenUpdateAsync(bot.config.token).onFailure {
          logger.error("PayloadCmdHandler[${this.uid()}] 无法更新 Access Token", it)
        }
        heartbeat()
      }.apply {
        logger.debug("WebSocket[${ws.hashCode()}] 心跳[$this]已启动")
//                logger.info("PayloadCmdHandler[${pch.uid()}] 状态设置为开启")
      }
    }
    openHeartbeat = true
  }

  private fun heartbeat() {
    if (ws.isClosed) return
    val payload = Payload(opcode = 1, hid = id)
    if (debugLog && debugHeartbeat) logger.debug("ws send(1): Heartbeat Dispatch")
    send(payload)
  }

  fun close() {
    vertx.cancelTimer(heartbeatId)
    heartbeatId = 0
    openHeartbeat = false
    logger.debug("WebSocket[${ws.hashCode()}] 心跳[${heartbeatId}]被关闭")
//        logger.info("PayloadCmdHandler[${this.uid()}] 状态设置为关闭")
  }

  private fun send(payload: Payload) {
    if (debugMataData) logger.debug("WebSocket[${ws.hashCode()}]  ws send: {}", payload)
    ws.writeTextMessage(payload.toJsonString())
  }


  fun uid(): String {
    return "${ws.hashCode()}"
  }
}
