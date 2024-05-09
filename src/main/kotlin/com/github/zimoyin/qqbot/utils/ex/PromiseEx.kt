package com.github.zimoyin.qqbot.utils.ex

import io.vertx.core.Promise

/**
 *
 * @author : zimo
 * @date : 2023/12/21
 */
fun <T> promise(): Promise<T> = Promise.promise<T>()
fun <T> Promise<T>.isCompleted(): Boolean = this.future().isComplete
fun <T> Promise<T>.isSucceeded(): Boolean = this.future().succeeded()
fun <T> Promise<T>.isFailed(): Boolean = this.future().failed()

/**
 * 是否有监听器存在。
 * 注意：如果 Promise 已经 complete，则返回 false
 */
fun <T> Promise<T>.isNotListener(): Boolean {
    val clazz2 = this::class.java
    val superclass = clazz2.superclass
    val field1 = superclass.getDeclaredField("listener") // 监听器，如果未有监听或者监听已经触发完毕了
    field1.isAccessible = true
    val listener = field1.get(this)
    return listener == null && !isCompleted()
}


/**
 * 是否是初始状态
 *  Promise 没有 complete，并且没有监听器
 */
fun <T> Promise<T>.isInitialStage(): Boolean = !isCompleted() && isNotListener()

