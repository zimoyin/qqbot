import com.github.zimoyin.qqbot.bot.message.type.KeyboardMessage
import com.github.zimoyin.qqbot.net.bean.message.send.SendMessageBean
import com.github.zimoyin.qqbot.utils.ex.toJsonObject
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.jsonObjectOf

/**
 *
 * @author : zimo
 * @date : 2023/12/26
 */


suspend fun main() {
    println(BVBBB(AAA.A).toJsonObject())
}
data class BVBBB(val a: AAA)
enum class AAA(val a: Int) {
    A(1),
    B(2)
}
