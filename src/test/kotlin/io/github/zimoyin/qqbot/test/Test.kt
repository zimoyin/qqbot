import com.fasterxml.jackson.databind.ObjectMapper
import com.github.zimoyin.qqbot.utils.ex.toJsonObject
import java.net.URI

/**
 *
 * @author : zimo
 * @date : 2023/12/26
 */


suspend fun main() {
    val uri = URI.create("http://cdn.pixabay.com/video/2023/08/11/175587-853887900_large.mp4?a")
    println(
        "${uri.host?:""}${uri.path?:""}${uri.query?.let { "?$it" }?:""}"
    )
}
