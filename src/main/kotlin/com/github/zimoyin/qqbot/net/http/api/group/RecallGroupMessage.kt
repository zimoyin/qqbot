package com.github.zimoyin.qqbot.net.http.api.group

import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.contact.Group
import com.github.zimoyin.qqbot.exception.HttpClientException
import com.github.zimoyin.qqbot.net.http.addRestfulParam
import com.github.zimoyin.qqbot.net.http.api.API
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future


/**
 * 撤回通道内的消息
 * @param group 群组
 * @param messageID 消息ID
 */
@UntestedApi
fun HttpAPIClient.recallGroupMessage(
    group: Group,
    messageID: String,
    callback: ((Boolean) -> Unit)? = null,
): Future<Boolean> {
    val promise = promise<Boolean>()
    API.RecallGroupMyMessage
        .putHeaders(group.botInfo.token.getHeaders())
        .addRestfulParam(group.id, messageID)
        .send()
        .bodyJsonHandle(promise, "recallGroupMessage", "撤回消息失败") {
            if (it.result) {
                promise.complete(true)
                callback?.let { it1 -> it1(true) }
            } else {
                //                promise.complete(false)
                logPreError(
                    promise, "recallGroupMessage", it.errorMessage ?: "未知错误,导致撤回失败"

                ).let { isLog ->
                    if (!promise.tryFail(HttpClientException(it.errorMessage ?: "未知错误,导致撤回失败"))) {
                        if (!isLog) logError(
                            "recallGroupMessage", "撤回消息失败", it.errorMessage ?: "未知错误,导致撤回失败"
                        )
                    }
                }
                callback?.let { it1 -> it1(false) }
            }
        }
    return promise.future()
}
