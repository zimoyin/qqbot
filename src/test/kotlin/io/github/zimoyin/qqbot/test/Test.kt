import com.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.message.type.CustomKeyboard
import com.github.zimoyin.qqbot.bot.message.type.KeyboardMessage
import com.github.zimoyin.qqbot.bot.message.type.customKeyboard
import com.github.zimoyin.qqbot.net.bean.message.MessageMarkdown
import com.github.zimoyin.qqbot.utils.ex.isInitialStage
import com.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future
import io.vertx.core.Vertx
import java.time.LocalDate
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

/**
 *
 * @author : zimo
 * @date : 2023/12/26
 */
suspend fun main(){
    println(
        MessageMarkdown.create("102077167_1706091638")
            .appendParam("date", LocalDate.now().toString())
            .appendParam("rw", "你好")
            .build().markdown.toJson()
    )
}
