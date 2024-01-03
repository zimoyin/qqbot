import com.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.json.JsonObject

/**
 *
 * @author : zimo
 * @date : 2023/12/26
 */


fun main() {
    val promise = promise<String>()

    promise.complete("a")
    promise.tryComplete("b")
    promise.fail("c")

    promise.future().onSuccess {
        println(it)
    }.onFailure {
        println(it)
    }
}
