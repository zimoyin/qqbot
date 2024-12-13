package io.github.zimoyin.qqbot.test.demo

import com.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import com.github.zimoyin.qqbot.utils.io
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
