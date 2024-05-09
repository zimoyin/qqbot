import com.github.zimoyin.qqbot.utils.ex.isCompleted
import com.github.zimoyin.qqbot.utils.ex.isInitialStage
import com.github.zimoyin.qqbot.utils.ex.isNotListener
import com.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.impl.future.PromiseImpl

/**
 *
 * @author : zimo
 * @date : 2023/12/26
 */


suspend fun main() {
    val promise = promise<String>()
    val future = promise.future()
    println(future::class.java)
    future.onFailure {
        println("58")
    }
//    promise.tryFail("")



    println(promise.isInitialStage())
}
