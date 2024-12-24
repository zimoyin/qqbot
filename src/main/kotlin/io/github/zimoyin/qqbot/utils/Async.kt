package io.github.zimoyin.qqbot.utils

import io.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import io.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Context
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.impl.WorkerPool
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.*

/**
 * 在后台线程执行非挂起的代码块。
 *
 * @author : zimo
 * @date : 2023/12/21
 * @param callback 需要在后台线程执行的非挂起代码块。
 * @return 一个 [Job] 对象，用于管理协程的生命周期。
 */
fun io(callback: () -> Unit): Job = CoroutineScope(Dispatchers.IO).launch {
    callback()
}


/**
 * 在后台线程执行非挂起的代码块。
 *
 * @author : zimo
 * @date : 2023/12/21
 * @param callback 需要在后台线程执行的非挂起代码块。
 * @return 一个 [Job] 对象，用于管理协程的生命周期。
 */
fun cpu(callback: () -> Unit): Job = CoroutineScope(Dispatchers.Default).launch {
    callback()
}

fun coroutine(callback: suspend () -> Unit): Job = CoroutineScope(Dispatchers.vertx()).launch {
    callback()
}


/**
 * 在后台线程执行挂起的代码块，并返回一个 [Deferred] 对象，用于获取执行结果。使用 await 可以挂起协程并等待结果
 * 注意调用 await 后不会阻塞线程但是会阻塞协程。
 * @author : zimo
 * @date : 2023/12/21
 * @param callback 需要在后台线程执行的挂起代码块。
 * @return 一个 [Deferred] 对象，可以用于等待执行完成并获取结果。
 */
fun <T> async(callback: suspend () -> T): Deferred<T> = CoroutineScope(Dispatchers.vertx()).async {
    callback()
}


/**
 * 在后台线程执行挂起的代码块，并返回一个 [Future] 对象，用于获取执行结果。使用 await 可以挂起协程并等待结果
 */
fun <T> task(callback: suspend (Promise<T>) -> T): Future<T> {
    val promise = promise<T>()
    CoroutineScope(Dispatchers.vertx()).launch {
        kotlin.runCatching {
            callback(promise)
        }.onFailure {
            promise.tryFail(it)
        }.onSuccess {
            promise.tryComplete(it)
        }
    }
    return promise.future()
}

/**
 * 获取一个与 Vertx 事件线程池对应的协程调度器。
 * 该调度器使用的是 vertx 事件循环器，因此禁止在该调度器中执行高CPU任务与阻塞任务
 * 对于网络，IO, 文件等操作，推荐使用 vertx 的异步方法，或者使用 vertx 的线程池
 */
fun Dispatchers.vertx(vertx: Vertx = GLOBAL_VERTX_INSTANCE): CoroutineDispatcher {
    return vertx.dispatcher()
}

/**
 * 获取一个与 Vertx 工作线程对应的协程调度器。
 */
fun Dispatchers.vertxWorker(vertx: Vertx = GLOBAL_VERTX_INSTANCE): CoroutineDispatcher {
    val worker = vertx.orCreateContext.get<ExecutorCoroutineDispatcher>("worker_ExecutorCoroutineDispatcher") ?: run {
        var context = vertx.orCreateContext
        val workerPoolField = try {
            context::class.java.getDeclaredField("workerPool").apply { isAccessible = true }
        } catch (e: NoSuchFieldException) {
            val delegateField = context::class.java.getDeclaredField("delegate").apply { isAccessible = true }
            context = delegateField.get(context) as Context
            context::class.java.getDeclaredField("workerPool").apply { isAccessible = true }
        }
        (workerPoolField.get(context) as WorkerPool).executor().asCoroutineDispatcher()
    }
    vertx.orCreateContext.put("worker_ExecutorCoroutineDispatcher", worker)
    return worker
}
