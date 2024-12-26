package io.github.zimoyin.qqbot.test

import java.net.URI
import java.net.URL

/**
 *
 * @author : zimo
 * @date : 2024/12/26
 */

fun main() {
    val url = URI("http://127.0.0.1:80/websocket")

    println(url.scheme)
    println(url.path)
    println(url.host)
    println(url.port)
}
