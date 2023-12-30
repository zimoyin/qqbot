package com.github.zimoyin.qqbot.utils.ex

import io.vertx.core.Promise

/**
 *
 * @author : zimo
 * @date : 2023/12/21
 */
fun <T> promise(): Promise<T> = Promise.promise<T>()