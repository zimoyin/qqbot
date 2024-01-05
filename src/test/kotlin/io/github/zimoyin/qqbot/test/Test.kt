import com.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import com.github.zimoyin.qqbot.utils.ex.promise
import com.github.zimoyin.qqbot.utils.vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.coAwait
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *
 * @author : zimo
 * @date : 2023/12/26
 */


suspend fun main() {
    CoroutineScope(Dispatchers.vertx(GLOBAL_VERTX_INSTANCE)).launch {
        val promise = promise<Boolean>()
//    promise.fail("aa")
        promise.complete(true)
        promise.future().coAwait()

        val promise1 = promise<Boolean>()
        promise1.fail("aa")
        promise1.future().coAwait()
    }
}
