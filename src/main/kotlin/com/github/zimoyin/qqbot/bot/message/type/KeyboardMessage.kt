package com.github.zimoyin.qqbot.bot.message.type

import com.github.zimoyin.qqbot.utils.ex.toJsonObject
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.jsonObjectOf
import org.intellij.lang.annotations.Language

/**
 *
 * @author : zimo
 * @date : 2024/01/27
 *
 * TODO 后续支持 keyboard 对象
 */
data class KeyboardMessage(
    @Language("json") val keyboard: String,
) : MessageItem {


    companion object {
        @JvmStatic
        fun create(id: String): KeyboardMessage {
            return KeyboardMessage(jsonObjectOf("id" to id).encode())
        }
    }

    override fun toString(): String {
        return keyboard
    }
}
