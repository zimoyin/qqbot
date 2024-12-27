package io.github.zimoyin.qqbot.test

import io.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import java.net.URI

/**
 *
 * @author : zimo
 * @date : 2024/12/26
 */

fun main() {
   GLOBAL_VERTX_INSTANCE.setTimer(-1){
       println("Hello World")
   }
}
