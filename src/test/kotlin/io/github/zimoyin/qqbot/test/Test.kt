import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.github.zimoyin.qqbot.net.bean.message.Message
import com.github.zimoyin.qqbot.net.http.DefaultHttpClient
import com.github.zimoyin.qqbot.utils.JSON
import com.github.zimoyin.qqbot.utils.ex.toJAny
import com.github.zimoyin.qqbot.utils.ex.toJsonObject
import io.vertx.core.json.JsonObject
import java.net.URI
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

/**
 *
 * @author : zimo
 * @date : 2023/12/26
 */


suspend fun main() {
    println(DefaultHttpClient.get("https://cn.apihz.cn/api/zici/today.php?id=88888888&key=88888888").bodyAsString())
}
