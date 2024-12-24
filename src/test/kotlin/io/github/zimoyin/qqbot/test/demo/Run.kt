package io.github.zimoyin.qqbot.test.demo

import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import io.github.zimoyin.qqbot.utils.io
import openDebug
import token

/**
 *
 * @author : zimo
 * @date : 2024/11/26
 */
fun main() {
    openDebug()
    TMain.run(token)
}
