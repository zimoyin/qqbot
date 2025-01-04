package io.github.zimoyin.ra3.ex

import kotlinx.coroutines.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * 创建一个线程，并执行一个代码块。
 */
fun thread(callback: () -> Unit): Thread {
    return Thread { callback() }.apply { start() }
}

/**
 * 启动一个虚拟线程，如果没有虚拟线程则启动一个协程
 */
fun <T> CoroutineScope.virtualThread(block: suspend CoroutineScope.() -> T): Future<T> {
    val future = CompletableFuture<T>()

    val job = Job()
    if (virtualThreadExecutor != null) {
        return virtualThreadExecutor!!.submit {
            runBlocking(coroutineContext + job) {
                block()
            }
        } as Future<T>
    }

    launch {
        kotlin.runCatching { block().apply { future.complete(this) } }.onFailure {
            future.completeExceptionally(it)
        }
    }
    return future
}

/**
 * 启动一个虚拟线程，如果没有虚拟线程则启动一个IO协程
 */
fun <T> virtualThread(block: suspend CoroutineScope.() -> T): Future<T> {
    val future = CompletableFuture<T>()
    if (virtualThreadExecutor != null) {
        return virtualThreadExecutor!!.submit {
            runBlocking(block = block)
        } as Future<T>
    }

    CoroutineScope(Dispatchers.Default).launch {
        kotlin.runCatching { block().apply { future.complete(this) } }.onFailure {
            future.completeExceptionally(it)
        }
    }
    return future
}

/**
 * 创建一个虚拟线程池
 */
val virtualThreadExecutor: ExecutorService? by lazy {
    runCatching {
        val clazz = Executors::class.java
        val method = clazz.getMethod("newVirtualThreadPerTaskExecutor")
        method.isAccessible = true
        method.invoke(null) as ExecutorService
    }.getOrNull()
}