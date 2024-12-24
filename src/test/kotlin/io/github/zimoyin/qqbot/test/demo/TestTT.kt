package io.github.zimoyin.qqbot.test.demo

import io.github.zimoyin.qqbot.utils.ex.await
import io.github.zimoyin.qqbot.utils.ex.toJsonObject
import io.vertx.core.http.WebSocketClientOptions
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.coroutines.CoroutineVerticle
import java.net.URL

/**
 *
 * @author : zimo
 * @date : 2024/12/22
 */
class TestTT :CoroutineVerticle(){
    override suspend fun start() {
        kotlin.runCatching {
            val url = URL("http://pokemonemerald.game.lingyuzhao.top:8878/chat")
            val options = WebSocketClientOptions()
                .setConnectTimeout(6000)
                .setSsl(true)
                .setTrustAll(true)
            vertx.createWebSocketClient(options).connect(url.port, url.host, url.toURI().toString()).onSuccess {
                println("Connected to server")
                it.textMessageHandler {message->
                    // 输出收到的消息，方便调试
                    println("Received message: $message")
                    val messageJson = message.toJsonObject()
                }
                it.writeTextMessage(jsonObjectOf(
                    "roomid" to "8848",
                    "imid" to "8848",
                    "msg" to "132"
                ).encode())
                it.closeHandler {
                    println("WebSocket connection closed")
                }
            }.onFailure {
                println("Failed to connect to server: $it")
            }
        }.onFailure {
            println("Failed to connect to server: $it")
        }
    }
}
