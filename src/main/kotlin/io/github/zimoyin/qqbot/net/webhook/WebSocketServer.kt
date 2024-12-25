package io.github.zimoyin.qqbot.net.webhook

import io.github.zimoyin.qqbot.LocalLogger
import io.github.zimoyin.qqbot.utils.ex.isInitialStage
import io.vertx.core.Promise
import io.vertx.kotlin.coroutines.CoroutineVerticle

/**
 *
 * @author : zimo
 * @date : 2024/12/21
 */
class WebSocketServer(val promise: Promise<WebSocketServer>) : CoroutineVerticle() {
    val logger = LocalLogger(this::class.java)

    override suspend fun start() {
        kotlin.runCatching {
//            vertx.create
        }.onFailure {
            if (promise.isInitialStage()) logger.error("创建WebSocketServer失败",it)
            promise.tryFail(it)
        }
    }

}
