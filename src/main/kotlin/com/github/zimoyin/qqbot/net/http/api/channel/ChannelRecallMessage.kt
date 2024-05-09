package com.github.zimoyin.qqbot.net.http.api.channel

import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.exception.HttpClientException
import com.github.zimoyin.qqbot.net.http.addRestfulParam
import com.github.zimoyin.qqbot.net.http.api.API
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future


/**
 * 撤回通道内的消息
 * @param channel 频道
 * @param messageID 消息ID
 * @param hidetip 是否隐藏提示小灰条
 */
@UntestedApi
fun HttpAPIClient.recallChannelMessage(
    channel: Channel,
    messageID: String,
    hidetip: Boolean = false,
    callback: ((Boolean) -> Unit)? = null,
): Future<Boolean> {
    val promise = promise<Boolean>()
    API.RecallChannelMessage
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.channelID!!, messageID)
        .addQueryParam("hidetip", hidetip.toString())
        .send()
        .bodyJsonHandle(promise, "recallChannelMessage", "撤回消息失败") {
            if (it.result) {
                promise.complete(true)
                callback?.let { it1 -> it1(true) }
            } else {
                //                promise.complete(false)
                logPreError(promise,"recallChannelProvateMessage", it.errorMessage ?: "未知错误,导致撤回失败").let { isLog->
                    if (!promise.tryFail(HttpClientException(it.errorMessage ?: "未知错误,导致撤回失败"))) {
                        logError("recallChannelProvateMessage", it.errorMessage ?: "未知错误,导致撤回失败")
                    }
                }

                callback?.let { it1 -> it1(false) }
            }
        }
    return promise.future()
}


/**
 * 撤回频道私信的信息
 * @param channel 频道
 * @param messageID 消息ID
 * @param hidetip 是否隐藏提示小灰条
 */
@UntestedApi
fun HttpAPIClient.recallChannelPrivateMessage(
    channel: Channel,
    messageID: String,
    hidetip: Boolean = false,
    callback: ((Boolean) -> Unit)? = null,
): Future<Boolean> {
    val promise = promise<Boolean>()
    API.RecallChannelMyPrivateMessage
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.guildID, messageID)
        .addQueryParam("hidetip", hidetip.toString())
        .send()
        .bodyJsonHandle(promise, "recallChannelProvateMessage", "撤回消息失败") {
            if (it.result) {
                promise.complete(true)
                callback?.let { it1 -> it1(true) }
            } else {
//                promise.complete(false)
                logPreError(promise,"recallChannelMyProvateMessage", it.errorMessage ?: "未知错误,导致撤回失败").let { isLog->
                    if (!promise.tryFail(HttpClientException(it.errorMessage ?: "未知错误,导致撤回失败"))) {
                        logError("recallChannelMyProvateMessage", it.errorMessage ?: "未知错误,导致撤回失败")
                    }
                }
                callback?.let { it1 -> it1(false) }
            }
        }
    return promise.future()
}
