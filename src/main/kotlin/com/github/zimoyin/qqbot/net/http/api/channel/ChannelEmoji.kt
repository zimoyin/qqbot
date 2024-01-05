package com.github.zimoyin.qqbot.net.http.api.channel

import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.bot.message.EmojiType
import com.github.zimoyin.qqbot.net.http.addRestfulParam
import com.github.zimoyin.qqbot.net.http.api.API
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future


/**
 * 添加表情
 *
 * @param messageID 消息ID
 * @param emojiType 表情类型
 * @author: zimo
 * @date:   2024/1/5 005
 */
@UntestedApi
fun HttpAPIClient.addEmoji(
    channel: Channel,
    messageID: String,
    emojiType: EmojiType,
    callback: ((Boolean) -> Unit)? = null,
): Future<Boolean> {
    val promise = promise<Boolean>()
    API.AddEmoji
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.channelID!!, messageID, emojiType.type, emojiType.id)
        .send()
        .bodyJsonHandle(promise, "addEmoji", "添加表情失败") {
            if (it.result) {
                promise.complete(true)
                callback?.let { it1 -> it1(true) }
            } else {
                promise.complete(false)
                callback?.let { it1 -> it1(false) }
            }
        }
    return promise.future()
}

/**
 * 删除表情
 * @param messageID 消息ID
 * @param emojiType 表情类型
 * @author: zimo
 * @date:   2024/1/5 005
 */
@UntestedApi
fun HttpAPIClient.deleteEmoji(
    channel: Channel,
    messageID: String,
    emojiType: EmojiType,
    callback: ((Boolean) -> Unit)? = null,
): Future<Boolean> {
    val promise = promise<Boolean>()
    API.DeleteEmoji
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.channelID!!, messageID, emojiType.type, emojiType.id)
        .send()
        .bodyJsonHandle(promise, "deleteEmoji", "删除表情失败") {
            if (it.result) {
                promise.complete(true)
                callback?.let { it1 -> it1(true) }
            } else {
                promise.complete(false)
                callback?.let { it1 -> it1(false) }
            }
        }
    return promise.future()
}
