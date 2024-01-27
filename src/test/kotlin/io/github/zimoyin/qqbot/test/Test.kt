import com.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import com.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import com.github.zimoyin.qqbot.net.bean.MessageMarkdown
import com.github.zimoyin.qqbot.net.bean.MessageMarkdownParam
import com.github.zimoyin.qqbot.utils.JSON
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
    val p1 = MessageMarkdownParam.create("date", "01/23 024")
    val p2 = MessageMarkdownParam.create("rw", "通知内容")
    val chain = MessageChainBuilder().append(
        MessageMarkdown(
            "102077167_1706091638",
            p1.add(p2)
        ).toMessage()
    ).setID("8848").build()
    println(JSON.toJsonString(chain.convertChannelMessage()))
}
