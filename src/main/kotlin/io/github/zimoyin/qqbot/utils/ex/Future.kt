package io.github.zimoyin.qqbot.utils.ex

import io.vertx.core.Future
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 *
 * @author : zimo
 * @date : 2023/12/07
 */


/**
 * 将异步 Future 封装为同步的 Promise,并等待结果返回
 */
fun <T> Future<T>.awaitToCompleteExceptionally(callback: ((T) -> Unit)? = null): T = CompletableFuture<T>().let {
    onComplete { asyncResult ->
        if (asyncResult.succeeded()) {
            it.complete(asyncResult.result())
        } else {
            it.completeExceptionally(asyncResult.cause())
        }
    }
    return it.get().apply {
        if (callback!=null) callback(this)
    }
}

/**
 * 将 Vert.x 的 [Future] 对象转换为挂起函数，以在协程中使用。
 *
 * 该函数通过 [suspendCancellableCoroutine] 创建一个挂起函数，它允许在协程中等待异步操作完成。
 *
 * @return 异步操作的结果，如果操作失败，则抛出异常。
 *
 * @param T 异步操作的结果类型。
 */
suspend fun <T> Future<T>.await(): T = suspendCancellableCoroutine { continuation ->
    onComplete { asyncResult ->
        if (asyncResult.succeeded()) {
            continuation.resume(asyncResult.result())
        } else {
            continuation.resumeWithException(asyncResult.cause())
        }
    }
}
