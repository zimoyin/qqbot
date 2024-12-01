import com.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import com.github.zimoyin.qqbot.bot.message.type.KeyboardMessage
import com.github.zimoyin.qqbot.net.bean.message.send.SendMessageBean
import com.github.zimoyin.qqbot.utils.vertxWorker
import io.vertx.kotlin.core.json.jsonObjectOf
import kotlinx.coroutines.*
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher
import kotlin.system.measureTimeMillis

/**
 *
 * @author : zimo
 * @date : 2023/12/26
 */


suspend fun main() {
    val keyboardMessage = KeyboardMessage.create("5")
    SendMessageBean(
        content = null,
        embed = null,
        ark = null,
        messageReference = null,
        imageURI = null,
        id = null,
        eventID = null,
        markdown = null,
        keyboard = keyboardMessage,
        channelFile = null,
        channelFileBytes = null,
        videoURI = null,
        audioURI = null,
        msgType = null,
        media = null
    ).toJson().apply {
        println(this)
    }
    println(keyboardMessage)
}
