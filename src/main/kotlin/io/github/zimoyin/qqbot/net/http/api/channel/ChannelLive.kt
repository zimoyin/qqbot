package io.github.zimoyin.qqbot.net.http.api.channel

import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.net.http.addRestfulParam
import io.github.zimoyin.qqbot.net.http.api.API
import io.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future


/**
 * 机器人上麦
 *
 * @param channel 频道
 * @param callback 回调
 * @author: zimo
 * @date:   2024/1/5 005
 */
@UntestedApi
fun HttpAPIClient.robotOnStage(
    channel: Channel,
    callback: ((Boolean) -> Unit)? = null,
): Future<Boolean> {
    val promise = promise<Boolean>()
    API.RobotOnStage
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.channelID!!)
        .send()
        .bodyJsonHandle(promise, "robotOnStage", "机器人上麦失败") {
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
 * 机器人下麦
 *
 * @param channel 频道
 * @param callback 回调
 * @author: zimo
 * @date:   2024/1/5 005
 */
@UntestedApi
fun HttpAPIClient.robotOffStage(
    channel: Channel,
    callback: ((Boolean) -> Unit)? = null,
): Future<Boolean> {
    val promise = promise<Boolean>()
    API.RobotOffStage
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.channelID!!)
        .send()
        .bodyJsonHandle(promise, "robotOffStage", "机器人下麦失败") {
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
