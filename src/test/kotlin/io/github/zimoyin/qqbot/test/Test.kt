import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.github.zimoyin.qqbot.net.bean.message.Message
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
    val create = URI.create("http://a/a?a=2")
    println(create.scheme)
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class A(
    // "2024-05-12T23:51:58+08:00"
    val timestamp:Instant? = null
)
