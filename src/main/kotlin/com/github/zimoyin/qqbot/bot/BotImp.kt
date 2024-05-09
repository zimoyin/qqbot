package com.github.zimoyin.qqbot.bot

import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.exception.HttpClientException
import com.github.zimoyin.qqbot.net.Token
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.net.http.api.accessTokenUpdateAsync
import com.github.zimoyin.qqbot.net.http.api.botInfo
import com.github.zimoyin.qqbot.net.websocket.WebsocketClient
import com.github.zimoyin.qqbot.utils.ex.awaitToCompleteExceptionally
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.http.WebSocket
import org.slf4j.LoggerFactory

/**
 *
 * @author : zimo
 * @date : 2023/12/06/23:55
 */
class BotImp(
    val token: Token,
    override val context: BotContent = BotContent(),
    override val config: BotConfig = BotConfigBuilder().setToken(token).build(),
) : Bot {
    private val logger = LoggerFactory.getLogger(BotImp::class.java)
    private var websocketClient: WebsocketClient? = null
    private val vertx = config.vertx

    override val avatar: String
    override val nick: String
    override val unionOpenid: String
    override val unionUserAccount: String
    override val id: String
    override val botInfo: BotInfo by lazy {
        BotInfo.create(this)
    }

    init {
        try {
            val user = HttpAPIClient.botInfo(token).awaitToCompleteExceptionally()
            avatar = user.avatar ?: ""
            nick = user.username
            unionOpenid = user.unionOpenID ?: ""
            unionUserAccount = user.unionUserAccount ?: ""
            id = user.id
        } catch (e: NullPointerException) {
            throw HttpClientException("Unable to provide specific information about the robot", e)
        }
    }


    override fun login(): Future<WebSocket> {
        val promise = Promise.promise<WebSocket>()
        if (websocketClient != null) {
            return Future.failedFuture(IllegalStateException("Web socket has already been started"))
        }
        this.context["internal.promise"] = promise
        if (token.version == 2) {
            HttpAPIClient.accessTokenUpdateAsync(token).onSuccess {
                websocketClient = WebsocketClient(this)
                vertx.deployVerticle(websocketClient).onFailure {
                    if (!promise.tryFail(it)) logger.error("无法启动 ws 客户端", it)
                }
            }.onFailure {
                if (!promise.tryFail(it)) logger.error("无法获取到 Access Token，禁止启动 ws 客户端", it)
            }
        } else if (token.version == 1) {
            websocketClient = WebsocketClient(this)
            vertx.deployVerticle(websocketClient).onFailure {
                if (!promise.tryFail(it)) logger.error("无法启动 ws 客户端", it)
            }
        } else {
            val exception = IllegalArgumentException("Unsupported version: ${token.version}")
            if (!promise.tryFail(exception)) logger.error("无法启动 ws 客户端", exception)
        }
        // 返回
        return promise.future().onSuccess {
            if (websocketClient == null) {
                promise.tryFail(NullPointerException("websocketClient is null"))
            } else {
                this.context["internal.websocketClient"] = websocketClient
            }
        }
    }

    override fun close() {
        if (websocketClient != null) websocketClient!!.close()
        this.context.clear()
        logger.info("the bot[${this.config.token.appID}] 上下文被清空")
    }

    override fun send(message: MessageChain): Future<MessageChain> {
        throw IllegalStateException("You cannot send yourself a message by yourself")
    }

    override fun toString(): String {
        return "BotImp(nick='$nick', id='$id')"
    }


}
