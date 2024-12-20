package com.github.zimoyin.qqbot.net.http.api.friend

import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.contact.Friend
import com.github.zimoyin.qqbot.exception.HttpClientException
import com.github.zimoyin.qqbot.net.http.addRestfulParam
import com.github.zimoyin.qqbot.net.http.api.API
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future


/**
 * 撤回通道内的消息
 * @param friend 好友
 * @param messageID 消息ID
 */
@UntestedApi
fun HttpAPIClient.recallFriendMessage(
    friend: Friend,
    messageID: String,
    callback: ((Boolean) -> Unit)? = null,
): Future<Boolean> {
    val promise = promise<Boolean>()
    API.RecallFriendMyMessage.putHeaders(friend.botInfo.token.getHeaders()).addRestfulParam(friend.id, messageID).send()
        .bodyJsonHandle(promise, "recallFriendMessage", "撤回消息失败") {
            if (it.result) {
                promise.complete(true)
                callback?.let { it1 -> it1(true) }
            } else {
                //                promise.complete(false)
                logPreError(
                    promise, "recallFriendMessage", it.errorMessage ?: "未知错误,导致撤回失败"
                ).let { isLog ->
                    if (!promise.tryFail(HttpClientException(it.errorMessage ?: "未知错误,导致撤回失败"))) {
                        if (!isLog) logError(
                            "recallFriendMessage", "撤回消息失败", it.errorMessage ?: "未知错误,导致撤回失败"
                        )
                    }
                }

                callback?.let { it1 -> it1(false) }
            }
        }
    return promise.future()
}
