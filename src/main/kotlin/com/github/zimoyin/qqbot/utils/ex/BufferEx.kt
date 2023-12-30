package com.github.zimoyin.qqbot.utils.ex

import io.vertx.core.buffer.Buffer
import java.nio.charset.Charset

fun Buffer.writeToText(charset: Charset = Charsets.UTF_8): String {
    return String(bytes, charset)
}

fun <T> Buffer.mapTo(cls:Class<T>): T {
    return this.toJsonObject().mapTo(cls)
}