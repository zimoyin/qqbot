package com.github.zimoyin.qqbot.net.http.api.channel

import com.github.zimoyin.qqbot.net.http.addRestfulParam
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.net.bean.MessageSetting
import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.net.http.api.API
import com.github.zimoyin.qqbot.utils.ex.promise
import com.github.zimoyin.qqbot.utils.ex.toJsonArray
import com.github.zimoyin.qqbot.utils.ex.writeToText
import io.vertx.core.Future
import io.vertx.core.json.JsonObject


/**
 * 删除子频道成员
 *
 * @param channel 频道
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/28 028
 */
@UntestedApi
fun HttpAPIClient.getChannelRate(
    channel: Channel,
    callback: ((MessageSetting) -> Unit)? = null,
): Future<MessageSetting> {
    val promise = promise<MessageSetting>()
    API.ChannelRate.addRestfulParam(channel.guildID).send().onSuccess {
        kotlin.runCatching {
            val json = it.bodyAsJsonObject()
            val code = json.getInteger("code")
            val message = json.getString("message")
            if (code == null && message == null) {
                val bean = json.mapTo(MessageSetting::class.java)
                promise.complete(bean)
                callback?.let { it1 -> it1(bean) }
            } else {
                logError(
                    "ChannelRate",
                    "result -> [$code] $message"
                )
            }
        }.onFailure {
            logError("ChannelRate", "获取频道消息频率的设置详情", it)
            promise.fail(it)
        }
    }.onFailure {
        logError("ChannelRate", "获取频道消息频率的设置详情", it)
        promise.fail(it)
    }
    return promise.future()
}

/**
 * 频道全员禁言
 *
 * @param channel 频道
 * @param muteTimestamp 禁言时长 (ms) 因为服务器精度问题只能精确到 second
 * @param muteEndTimestamp 禁言结束时间 (ms) 因为服务器精度问题只能精确到 second
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/28 028
 */
@UntestedApi
fun HttpAPIClient.setChannelMute(
    channel: Channel,
    muteTimestamp: Long,
    muteEndTimestamp: Long = System.currentTimeMillis() + muteTimestamp,
    callback: ((Boolean) -> Unit)? = null,
): Future<Boolean> {
    val promise = promise<Boolean>()
    val param = JsonObject().apply {
        put("mute_seconds", "${muteTimestamp / 1000}")
        put("mute_end_timestamp", "${muteEndTimestamp / 1000}")
    }
    API.ChannelMute.addRestfulParam(channel.guildID).sendJsonObject(param).onSuccess {
        kotlin.runCatching {
            if (it.statusCode() == 204) {
                promise.complete(true)
                callback?.let { it1 -> it1(true) }
            } else {
                promise.fail("Error: ${it.body().writeToText()}")
                logError(
                    "ChannelMute",
                    "result -> ${it.body().writeToText()}"
                )
                callback?.let { it1 -> it1(false) }
            }
        }.onFailure {
            logError("ChannelMute", "设置频道全体禁言失败", it)
            promise.fail(it)
        }
    }.onFailure {
        logError("ChannelMute", "设置频道全体禁言失败", it)
        promise.fail(it)
    }
    return promise.future()
}

/**
 * 指定频道成员禁言
 * 该接口同样支持解除指定成员禁言，将mute_end_timestamp或mute_seconds传值为字符串'0'即可
 * @param channel 频道
 * @param userID 用户ID
 * @param muteTimestamp 禁言时长 (ms) 因为服务器精度问题只能精确到 second
 * @param muteEndTimestamp 禁言结束时间 (ms) 因为服务器精度问题只能精确到 second
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/28 028
 */
@UntestedApi
fun HttpAPIClient.setChannelMuteMember(
    channel: Channel,
    userID: String,
    muteTimestamp: Long,
    muteEndTimestamp: Long = System.currentTimeMillis() + muteTimestamp,
    callback: ((Boolean) -> Unit)? = null,
): Future<Boolean> {
    val promise = promise<Boolean>()
    val param = JsonObject().apply {
        put("mute_seconds", "${muteTimestamp / 1000}")
        put("mute_end_timestamp", "${muteEndTimestamp / 1000}")
    }
    API.ChannelMuteMember.addRestfulParam(channel.guildID, userID).sendJsonObject(param).onSuccess {
        kotlin.runCatching {
            if (it.statusCode() == 204) {
                promise.complete(true)
                callback?.let { it1 -> it1(true) }
            } else {
                promise.fail("Error: ${it.body().writeToText()}")
                logError(
                    "ChannelMuteMember",
                    "result -> ${it.body().writeToText()}"
                )
                callback?.let { it1 -> it1(false) }
            }
        }.onFailure {
            logError("ChannelMuteMember", "设置频道指定成员禁言失败", it)
            promise.fail(it)
        }
    }.onFailure {
        logError("ChannelMuteMember", "设置频道指定成员禁言失败", it)
        promise.fail(it)
    }
    return promise.future()
}

/**
 * 批量频道成员禁言
 * 该接口同样支持解除指定成员禁言，将mute_end_timestamp或mute_seconds传值为字符串'0'即可
 * @param channel 频道
 * @param userID 用户ID
 * @param muteTimestamp 禁言时长 (ms) 因为服务器精度问题只能精确到 second
 * @param muteEndTimestamp 禁言结束时间 (ms) 因为服务器精度问题只能精确到 second
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/28 028
 */
@UntestedApi
fun HttpAPIClient.setChannelMuteMembers(
    channel: Channel,
    userIDs: List<String>,
    muteTimestamp: Long,
    muteEndTimestamp: Long = System.currentTimeMillis() + muteTimestamp,
    callback: ((Boolean) -> Unit)? = null,
): Future<Boolean> {
    val promise = promise<Boolean>()
    val param = JsonObject().apply {
        put("user_ids", userIDs.toJsonArray())
        put("mute_seconds", "${muteTimestamp / 1000}")
        put("mute_end_timestamp", "${muteEndTimestamp / 1000}")
    }
    API.ChannelMuteMembers.addRestfulParam(channel.guildID).sendJsonObject(param).onSuccess {
        kotlin.runCatching {
            if (it.statusCode() == 204) {
                promise.complete(true)
                callback?.let { it1 -> it1(true) }
            } else {
                promise.fail("Error: ${it.body().writeToText()}")
                logError(
                    "ChannelMuteMembers",
                    "result -> ${it.body().writeToText()}"
                )
                callback?.let { it1 -> it1(false) }
            }
        }.onFailure {
            logError("ChannelMuteMembers", "设置频道批量成员禁言失败", it)
            promise.fail(it)
        }
    }.onFailure {
        logError("ChannelMuteMembers", "设置频道批量成员禁言失败", it)
        promise.fail(it)
    }
    return promise.future()
}
