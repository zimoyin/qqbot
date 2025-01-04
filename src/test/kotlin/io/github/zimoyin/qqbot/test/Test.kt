package io.github.zimoyin.qqbot.test

import io.github.zimoyin.qqbot.utils.io
import io.github.zimoyin.qqbot.utils.vertxWorker
import io.vertx.core.Vertx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 *
 * @author : zimo
 * @date : 2024/12/26
 */
suspend fun main() {
    CoroutineScope(Dispatchers.vertxWorker()).launch {
        println(Vertx.currentContext())
    }
    println(Vertx.currentContext())
}
