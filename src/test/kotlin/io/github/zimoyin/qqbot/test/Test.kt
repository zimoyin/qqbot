import com.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import com.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import com.github.zimoyin.qqbot.bot.message.type.KeyboardMessage
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

    KeyboardMessage("{}")
}
