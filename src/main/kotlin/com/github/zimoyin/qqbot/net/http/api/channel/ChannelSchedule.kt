package com.github.zimoyin.qqbot.net.http.api.channel

import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.net.bean.ScheduleBean
import com.github.zimoyin.qqbot.net.http.addRestfulParam
import com.github.zimoyin.qqbot.net.http.api.API
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.utils.ex.mapTo
import com.github.zimoyin.qqbot.utils.ex.promise
import com.github.zimoyin.qqbot.utils.ex.toJsonObject
import io.vertx.core.Future
import io.vertx.kotlin.core.json.jsonObjectOf

/**
 * 获取频道日程列表
 *
 * @param channel 频道
 * @param since 起始时间戳 (单位ms) 若带了参数 since，则返回结束时间在 since 之后的日程列表；若未带参数 since，则默认返回当天的日程列表
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/31 031
 */
@UntestedApi
fun HttpAPIClient.getChannelScheduleList(
    channel: Channel,
    since: Long = System.currentTimeMillis(),
    callback: ((ScheduleBean) -> Unit)? = null,
): Future<ScheduleBean> {
    val promise = promise<ScheduleBean>()
    API.ChannelSchedules
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.channelID!!)
        .sendJsonObject(jsonObjectOf("since" to since))
        .bodyJsonHandle(promise, "getChannelScheduleList", "获取频道日程列表失败"){
            if (!it.result)  return@bodyJsonHandle
            val bean = it.body.mapTo(ScheduleBean::class.java)
            promise.complete(bean)
            callback?.let { it1 -> it1(bean) }
        }
    return promise.future()
}

/**
 * 获取频道日程详细详细
 *
 * @param channel 频道
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/31 031
 */
@UntestedApi
fun HttpAPIClient.getScheduleDetails(
    channel: Channel,
    scheduleID: String,
    callback: ((ScheduleBean) -> Unit)? = null,
): Future<ScheduleBean> {
    val promise = promise<ScheduleBean>()
    API.ChannelScheduleDetail
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.channelID!!, scheduleID)
        .send()
        .bodyJsonHandle(promise, "getScheduleDetails", "获取日程详情失败"){
            if (!it.result) return@bodyJsonHandle
            val bean = it.body.mapTo(ScheduleBean::class.java)
            promise.complete(bean)
            callback?.let { it1 -> it1(bean) }
        }
    return promise.future()
}

/**
 * 创建日程
 *
 * @param channel 频道
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/31 031
 */
@UntestedApi
fun HttpAPIClient.createSchedule(
    channel: Channel,
    schedule: ScheduleBean,
    callback: ((ScheduleBean) -> Unit)? = null,
): Future<ScheduleBean> {
    val promise = promise<ScheduleBean>()
    API.ChannelScheduleDetail
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.channelID!!)
        .sendJsonObject(jsonObjectOf("schedule" to schedule.toJsonObject()))
        .bodyJsonHandle(promise, "createSchedule", "创建日程失败"){
            if (!it.result) return@bodyJsonHandle
            val bean = it.body.mapTo(ScheduleBean::class.java)
            promise.complete(bean)
            callback?.let { it1 -> it1(bean) }
        }
    return promise.future()
}

/**
 * 修改日程
 *
 * @param channel 频道
 * @param schedule 日程
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/31 031
 */
@UntestedApi
fun HttpAPIClient.updateSchedule(
    channel: Channel,
    schedule: ScheduleBean,
    scheduleID: String,
    callback: ((ScheduleBean) -> Unit)? = null,
): Future<ScheduleBean> {
    val promise = promise<ScheduleBean>()
    API.UpdateChannelSchedule
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.channelID!!, scheduleID)
        .sendJsonObject(jsonObjectOf("schedule" to schedule.toJsonObject()))
        .bodyJsonHandle(promise, "updateSchedule", "修改日程失败") {
          if (!it.result) return@bodyJsonHandle
            val bean = it.body.mapTo(ScheduleBean::class.java)
            promise.complete(bean)
            callback?.let { it1 -> it1(bean) }

        }
    return promise.future()
}

/**
 * 删除日程
 *
 * @param channel 频道
 * @param scheduleID 日程ID
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/31 031
 */
@UntestedApi
fun HttpAPIClient.deleteSchedule(
    channel: Channel,
    scheduleID: String,
    callback: ((Boolean) -> Unit)? = null,
): Future<Boolean> {
    val promise = promise<Boolean>()
    API.UpdateChannelSchedule
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.channelID!!, scheduleID)
        .send()
        .bodyJsonHandle(promise, "deleteSchedule", "删除日程失败") {
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
