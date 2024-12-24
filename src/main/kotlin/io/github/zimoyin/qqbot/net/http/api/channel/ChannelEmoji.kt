package io.github.zimoyin.qqbot.net.http.api.channel

import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.message.EmojiType
import io.github.zimoyin.qqbot.exception.HttpHandlerException
import io.github.zimoyin.qqbot.net.bean.MemberBean
import io.github.zimoyin.qqbot.net.bean.User
import io.github.zimoyin.qqbot.net.http.addRestfulParam
import io.github.zimoyin.qqbot.net.http.api.API
import io.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.github.zimoyin.qqbot.utils.ex.await
import io.github.zimoyin.qqbot.utils.ex.awaitToCompleteExceptionally
import io.github.zimoyin.qqbot.utils.ex.promise
import io.github.zimoyin.qqbot.utils.io
import io.github.zimoyin.qqbot.utils.mapTo
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.get


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
    API.AddEmoji.putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.channelID!!, messageID, emojiType.type, emojiType.id).send()
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
    API.DeleteEmoji.putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.channelID!!, messageID, emojiType.type, emojiType.id).send()
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

/**
 * 获取表情用户列表
 * @author: zimo
 * @date:   2024/1/5 005
 */

@UntestedApi
fun HttpAPIClient.getEmojiUserList(
    channel: Channel,
    messageID: String,
    emojiType: EmojiType,
): Future<List<User>> {
    val promise = promise<List<User>>()

    io {
        val list = mutableListOf<User>()
        fun fetchEmojiUserList(cookie: String?) {
            API.GetEmojiUserList.putHeaders(channel.botInfo.token.getHeaders())
                .addRestfulParam(channel.channelID!!, messageID, emojiType.type, emojiType.id)
                .apply { cookie?.let { addQueryParam("cookie", it) } ?: addQueryParam("limit", "50") }.send()
                .awaitToCompleteExceptionally().apply {
                    kotlin.runCatching {
                        val json = bodyAsJsonObject()
                        val code = json?.getInteger("code")
                        val message = json?.getString("message")
                       if (code != null || message !=null) logPreError(promise, "getEmojiUserList", "获取表情用户列表失败").let {
                            if (!it) if (promise.tryFail("获取表情用户列表失败[$code]: $message")) {
                                logError("getEmojiUserList", "获取表情用户列表失败[$code]: $message")
                            }
                        }
                        return@runCatching json ?: return@apply
                    }.onFailure { e ->
                        logPreError(promise, "getEmojiUserList", "获取表情用户列表失败", e).let {
                            if (!it) if (promise.tryFail(e)) {
                                logError("getEmojiUserList", "获取表情用户列表失败", e)
                            }
                        }
                        return@apply
                    }.onSuccess {
                        list.addAll(it.getJsonArray("users")?.mapTo<User>()?:return@apply)
                        val isEnd = it.getBoolean("is_end") ?: return@apply
                        if (!isEnd) {
                            fetchEmojiUserList(it.getString("cookie") ?: return@apply)
                        }
                    }
                }
        }
        fetchEmojiUserList(null)
        promise.tryComplete(list)
    }

    return promise.future()
}

