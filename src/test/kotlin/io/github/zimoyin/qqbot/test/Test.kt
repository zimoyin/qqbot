import com.github.zimoyin.qqbot.bot.message.type.CustomKeyboard
import com.github.zimoyin.qqbot.bot.message.type.KeyboardMessage
import com.github.zimoyin.qqbot.bot.message.type.customKeyboard
import com.github.zimoyin.qqbot.net.bean.message.send.SendMessageBean
import com.github.zimoyin.qqbot.utils.ex.toJsonObject

/**
 *
 * @author : zimo
 * @date : 2023/12/26
 */


suspend fun main() {
    KeyboardMessage.createByContent(CustomKeyboard())
    val keyboard = customKeyboard {
        rows {
            buttons {
                button {
                    id = "1"
                    renderData {
                        label = "1"
                    }
                    action {
                        type = CustomKeyboard.ActionType.ActionTypeURL.value
                        data = "https://www.baidu.com"
                    }
                }
            }
        }
    }
}
