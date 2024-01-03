package com.github.zimoyin.qqbot.net.http.api.channel

import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.net.bean.PinsMessageBean
import com.github.zimoyin.qqbot.net.http.addRestfulParam
import com.github.zimoyin.qqbot.net.http.api.API
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.utils.ex.mapTo
import com.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future


/**
 * 创建精华消息
 *
 * @param channel 频道
 * @param messageID 消息ID
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/31 031
 */
@UntestedApi
fun HttpAPIClient.addEssentialMessage(
    channel: Channel,
    messageID: String,
    callback: ((PinsMessageBean) -> Unit)? = null,
): Future<PinsMessageBean> {
    val promise = promise<PinsMessageBean>()
    API.AddEssentialMessage
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.channelID!!, messageID)
        .send()
        .bodyJsonHandle(promise, "addEssentialMessage", "添加精华消息失败") {
            if (!it.result) return@bodyJsonHandle
            val bean = it.body.mapTo(PinsMessageBean::class.java)
            promise.complete(bean)
            callback?.let { it1 -> it1(bean) }

        }
    return promise.future()
}


/**
 * 删除精华信息
 * @param channel 频道
 * @param messageID 消息ID
 *
 * @author: zimo
 * @date:   2023/12/31 031
 */
@UntestedApi
fun HttpAPIClient.deleteEssentialMessage(
    channel: Channel,
    messageID: String,
    callback: ((Boolean) -> Unit)? = null,
): Future<Boolean> {
    val promise = promise<Boolean>()
    API.DeleteEssentialMessage
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.channelID!!, messageID)
        .send()
        .bodyJsonHandle(promise, "deleteEssentialMessage", "删除精华消息失败") {
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
 * 获取精华消息
 *
 * @param channel 频道
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/31 031
 */
@UntestedApi
fun HttpAPIClient.getEssentialMessages(
    channel: Channel,
    callback: ((PinsMessageBean) -> Unit)? = null,
): Future<PinsMessageBean> {
    val promise = promise<PinsMessageBean>()
    API.AddEssentialMessage
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.channelID!!)
        .send()
        .bodyJsonHandle(promise, "getEssentialMessages", "获取精华消息失败"){
            if (!it.result) return@bodyJsonHandle
            val bean = it.body.mapTo(PinsMessageBean::class.java)
            promise.complete(bean)
            callback?.let { it1 -> it1(bean) }
        }
    return promise.future()
}
