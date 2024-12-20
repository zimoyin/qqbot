import com.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.message.type.CustomKeyboard
import com.github.zimoyin.qqbot.bot.message.type.KeyboardMessage
import com.github.zimoyin.qqbot.bot.message.type.customKeyboard
import com.github.zimoyin.qqbot.utils.ex.isInitialStage
import com.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future
import io.vertx.core.Vertx
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

/**
 *
 * @author : zimo
 * @date : 2023/12/26
 */
suspend fun main(){
    val promise = promise<Boolean>()
    println(promise.isInitialStage())
    val future = promise.future()
    println(promise.isInitialStage())
    future.onFailure {
        println(it)
    }
    println(promise.isInitialStage())
    promise.tryFail("")
    println(promise.isInitialStage())

}
