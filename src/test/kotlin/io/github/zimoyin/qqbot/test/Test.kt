import com.github.zimoyin.qqbot.bot.message.type.CustomKeyboard
import com.github.zimoyin.qqbot.bot.message.type.KeyboardMessage
import com.github.zimoyin.qqbot.bot.message.type.customKeyboard

/**
 *
 * @author : zimo
 * @date : 2023/12/26
 */


suspend fun main() {
    KeyboardMessage.createByContent(CustomKeyboard())
    val keyboard = customKeyboard {
        row {
            button {
                id = "1"
                renderData {
                    label = "同意"
                    visitedLabel = "已同意"
                    style = 2
                }
                action {
                    type = CustomKeyboard.ActionType.ActionTypeCallback.value
                    permission {
                        type = CustomKeyboard.PermissionType.PermissionTypeAll.value
                    }
                    data = "data"
                    unsupportTips = "不支持按钮操作"
                    clickLimit = 10
                }
            }
        }
    }

    println(keyboard.toJson())
}
