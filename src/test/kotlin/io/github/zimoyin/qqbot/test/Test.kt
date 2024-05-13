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
    val jsonStr = """
        {
            "author": {
                "avatar": "http://thirdqq.qlogo.cn/g?b=oidb&k=TuzyS3SxOMHz9iboHPMnIuw&kti=ZiygqgAAAAI&s=0&t=1714200467",
                "bot": false,
                "id": "18263652895235093835",
                "username": "zimo"
            },
            "channel_id": "652108710",
            "content": "....",
            "guild_id": "7953093735127557650",
            "id": "0892cca69dd693c4af6e10a6c7f9b6023871489ecb83b206",
            "member": {
                "joined_at": "2024-05-08T20:38:34+08:00",
                "nick": "zimo",
                "roles": [
                    "14",
                    "1"
                ]
            },
            "seq": 113,
            "seq_in_channel": "113",
            "timestamp": "2024-05-12T23:51:58+08:00"
        }

    """.trimIndent()

//    println(Date(System.currentTimeMillis() * 1000))
    val toObject = JSON.toObject<A>(jsonStr)
    println(toObject.timestamp)
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class A(
    // "2024-05-12T23:51:58+08:00"
    val timestamp:Instant? = null
)
