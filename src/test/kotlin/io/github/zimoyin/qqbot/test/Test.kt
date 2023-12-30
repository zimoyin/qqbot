import com.github.zimoyin.qqbot.bot.Bot
import com.github.zimoyin.qqbot.net.Token


/**
 *
 * @author : zimo
 * @date : 2023/12/26
 */


fun main() {
    Bot.createBot(Token("102077167", "uW58JrUXnPH6AhfJhBBlhOMXy2eEQgpu", "HVYNyKSM3WmndEcl")).apply {
        login()
    }
}
