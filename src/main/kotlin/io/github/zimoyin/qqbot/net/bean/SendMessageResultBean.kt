package io.github.zimoyin.qqbot.net.bean

import io.github.zimoyin.qqbot.bot.contact.Contact
import io.vertx.core.Future
import java.io.Serializable

/**
 *
 * @author : zimo
 * @date : 2024/12/20
 */
data class SendMessageResultBean(
    val metadata: String = "",
    val msgID: String? = null,
    val contact: Contact,
):Serializable {
    fun recall(): Future<Boolean> {
        if (msgID.isNullOrEmpty()) return Future.failedFuture("消息ID为空")
        return contact.recall(msgID)
    }
}
