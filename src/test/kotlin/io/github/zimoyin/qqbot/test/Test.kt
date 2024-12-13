import com.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import com.github.zimoyin.qqbot.bot.message.type.CustomKeyboard
import com.github.zimoyin.qqbot.bot.message.type.KeyboardMessage
import com.github.zimoyin.qqbot.bot.message.type.customKeyboard
import io.vertx.core.Vertx
import kotlin.time.measureTime

/**
 *
 * @author : zimo
 * @date : 2023/12/26
 */
suspend fun main() {
    println(measureTime {
        Vertx.vertx()
    })
}
