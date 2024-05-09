package com.github.zimoyin.qqbot.net.http.api.channel

import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.bot.contact.ChannelUser
import com.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import com.github.zimoyin.qqbot.exception.HttpClientException
import com.github.zimoyin.qqbot.net.bean.ChannelBean
import com.github.zimoyin.qqbot.net.bean.GuildBean
import com.github.zimoyin.qqbot.net.bean.GuildRolesBean
import com.github.zimoyin.qqbot.net.bean.MemberBean
import com.github.zimoyin.qqbot.net.http.addRestfulParam
import com.github.zimoyin.qqbot.net.http.api.API
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.utils.JSON
import com.github.zimoyin.qqbot.utils.ex.await
import com.github.zimoyin.qqbot.utils.ex.mapTo
import com.github.zimoyin.qqbot.utils.ex.promise
import com.github.zimoyin.qqbot.utils.ex.writeToText
import com.github.zimoyin.qqbot.utils.mapTo
import com.github.zimoyin.qqbot.utils.task
import io.vertx.core.Future
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

/**
 *
 * 获取用户频道列表
 * @param info 机器人信息
 * @param callback 回调,回调中返回处理流中的 GuildBean 而不是最终的 Channel
 *
 * @author : zimo
 * @date : 2023/12/21
 */
fun HttpAPIClient.getGuilds(info: BotInfo, callback: ((GuildBean) -> Unit)? = null): Future<List<Channel>> {
    val promise = promise<List<Channel>>()
    API.GuildList
        .putHeaders(info.token.getHeaders())
        .send()
        .bodyJsonHandle(promise, "Guilds", "获取频道列表失败") { result ->
            if (!result.result) return@bodyJsonHandle
            result.body.toJsonArray().map {
                JSON.toObject<GuildBean>(it.toString())
            }.map {
                callback?.let { it1 -> it1(it) }
                ChannelImpl.convert(info, it.id!!, null, it.id)
            }.apply {
                promise.tryComplete(this)
            }
        }
    return promise.future()
}

/**
 *
 * 获取用户频道列表
 * @param info 机器人信息
 * @param callback 回调
 *
 * @author : zimo
 * @date : 2023/12/21
 */
fun HttpAPIClient.getGuildInfos(info: BotInfo, callback: ((List<GuildBean>) -> Unit)? = null): Future<List<GuildBean>> {
    val promise = promise<List<GuildBean>>()
    API.GuildList.putHeaders(info.token.getHeaders()).send()
        .bodyJsonHandle(promise, "Guilds", "获取频道列表失败") {
            if (!it.result) return@bodyJsonHandle
            it.body.toJsonArray().map {
                JSON.toObject<GuildBean>(it.toString())
            }.apply {
                promise.tryComplete(this)
                callback?.let { it1 -> it1(this) }
            }
        }
    return promise.future()
}


/**
 *
 * 获取频道列的详细信息
 * @param channel 频道
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/24 024
 */
fun HttpAPIClient.getGuildDetails(
    channel: Channel,
    callback: ((GuildBean) -> Unit)? = null,
): Future<GuildBean> {
    val info = channel.botInfo
    val promise = promise<GuildBean>()
    API.GuildDetails.addRestfulParam(channel.guildID).putHeaders(info.token.getHeaders()).send()
        .bodyJsonHandle(promise, "GuildDetails", "获取频道信息失败") {
            if (!it.result) return@bodyJsonHandle
            it.body.mapTo(GuildBean::class.java).let {
                promise.tryComplete(it)
                callback?.let { it1 ->
                    it1(it)
                }
            }
        }
    return promise.future()
}


/**
 *
 * 获取用户子频道列表
 * @param channel 频道
 * @param callback 回调
 *
 * @author : zimo
 * @date : 2023/12/21
 */
fun HttpAPIClient.getChannels(channel: Channel, callback: ((List<Channel>) -> Unit)? = null): Future<List<Channel>> {
    val promise = promise<List<Channel>>()
    API.Channels.addRestfulParam(channel.guildID).putHeaders(channel.botInfo.token.getHeaders()).send()
        .bodyJsonHandle(promise, "Channels", "获取子频道列表失败") {
            if (!it.result) return@bodyJsonHandle
            it.body.toJsonArray().map {
                JSON.toObject<ChannelBean>(it.toString())
            }.map {
                ChannelImpl.convert(channel.botInfo, it.guildID, it.channelID, it.id)
            }.apply {
                promise.tryComplete(this)
                callback?.let { it1 -> it1(this) }
            }
        }
    return promise.future()
}


/**
 *
 * 获取用户子频道列表
 * @param channel 频道
 * @param callback 回调
 *
 * @author : zimo
 * @date : 2023/12/21
 */
fun HttpAPIClient.getChannelInfos(
    channel: Channel,
    callback: ((List<ChannelBean>) -> Unit)? = null,
): Future<List<ChannelBean>> {
    val promise = promise<List<ChannelBean>>()
    API.Channels.addRestfulParam(channel.guildID).putHeaders(channel.botInfo.token.getHeaders()).send()
        .bodyJsonHandle(promise, "Channels", "获取子频道列表失败") {
            if (!it.result) return@bodyJsonHandle
            it.body.toJsonArray().map {
                JSON.toObject<ChannelBean>(it.toString())
            }.apply {
                promise.tryComplete(this)
                callback?.let { it1 -> it1(this) }
            }
        }
    return promise.future()
}


/**
 *
 * 获取子频道列的详细信息
 * @param channel 频道
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/24 024
 */
fun HttpAPIClient.getChannelDetails(
    channel: Channel,
    callback: ((ChannelBean) -> Unit)? = null,
): Future<ChannelBean> {
    val info = channel.botInfo
    val promise = promise<ChannelBean>()
    API.ChannelDetails.addRestfulParam(channel.channelID!!).putHeaders(info.token.getHeaders()).send()
        .bodyJsonHandle(promise, "ChannelDetails", "获取子频道信息失败") {
            if (!it.result) return@bodyJsonHandle
            it.body.mapTo(ChannelBean::class.java).let {
                promise.tryComplete(it)
                callback?.let { it1 ->
                    it1(it)
                }
            }
        }
    return promise.future()
}


/**
 *
 * 获取频道成员列表
 * @param channel 频道
 * @param after 上一次回包中最后一个member的user id， 如果是第一次请求填 0，默认为 0
 * @param limit 分页大小，1-400，默认是 1。成员较多的频道尽量使用较大的limit值，以减少请求数
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/24 024
 */
private fun HttpAPIClient.getGuildMemberPage(
    channel: Channel,
    after: String = "0",
    limit: Int = 400,
    callback: ((List<MemberBean>) -> Unit)? = null,
): Future<List<MemberBean>> {
    val info = channel.botInfo
    val promise = promise<List<MemberBean>>()
    API.GuildMembers.addRestfulParam(channel.guildID).putHeaders(info.token.getHeaders())
        .addQueryParam("after", after).addQueryParam("limit", limit.toString())
        .send()
        .bodyJsonHandle(promise, "GuildMembers", "获取频道人员信息失败") {
            if (!it.result) return@bodyJsonHandle
            it.body.toJsonArray().map {
                JSON.toObject<MemberBean>(it.toString())
            }.apply {
                promise.tryComplete(this)
                callback?.let { it1 -> it1(this) }
            }
        }
    return promise.future()
}


/**
 *
 * 获取频道成员列表
 * @param channel 频道
 * @param after 上一次回包中最后一个member的user id， 如果是第一次请求填 0，默认为 0
 * @param limit 分页大小，成员较多的频道尽量使用较大的limit值，以减少请求数.使用 -1 则不分页
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/24 024
 */
fun HttpAPIClient.getGuildMembers(
    channel: Channel,
    after: String = "0",
    limit: Int = -1,
    callback: ((List<ChannelUser>) -> Unit)? = null,
): Future<List<ChannelUser>> {
    val list = HashSet<MemberBean>()
    var margin = limit
    val promise = promise<List<ChannelUser>>()
    var id = after
    task {
        kotlin.runCatching {
            while (margin != 0) {
                val temp = if (margin <= -1) 400 else margin
                val beans = getGuildMemberPage(channel, id, temp).await()
                if (beans.isEmpty() || id == beans.last().user!!.uid) break
                beans.forEach {
                    if (margin == 0) return@forEach
                    list.add(it)
                    margin--
                }
                id = beans.last().user!!.uid
            }
        }.onFailure {
            logPreError(promise, "ChannelMembers", "获取频道成员失败", it).let { isLog ->
                if (!promise.tryFail(it) && !isLog) logError("ChannelMembers", "获取频道成员失败", it)
            }
        }.onSuccess {
            val result = list.toList().map { it.mapToChannelUser(channel) }
            promise.complete(result)
            callback?.let { it1 -> it1(result) }
        }
    }
    return promise.future()
}


/**
 * 获取在线频道成员数
 * @param channel 频道
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/24 024
 */
fun HttpAPIClient.getChannelOnlineMemberSize(
    channel: Channel,
    callback: ((Int) -> Unit)? = null,
): Future<Int> {
    val info = channel.botInfo
    val promise = promise<Int>()
    API.ChannelOnlineMemberSize.addRestfulParam(channel.channelID!!).putHeaders(info.token.getHeaders()).send()
        .bodyJsonHandle(promise, "ChannelOnlineMemberSize", "获取频道信息失败") {
            if (!it.result) return@bodyJsonHandle
            it.body.toJsonObject().getInteger("online_nums").let { count ->
                if (count != null) {
                    promise.complete(count)
                    callback?.let { it1 -> it1(count) }
                } else {
                    val e = HttpClientException("The API returned unexpected content: ${it.body.writeToText()}")
                    logPreError(promise, "ChannelOnlineMemberSize", "获取频道在线人数失败", e).let { isLog ->
                        if (!promise.tryFail(e)) {
                            if (!isLog) apiError("ChannelOnlineMemberSize", it.body.toJsonObject())
                        }
                    }
                }
            }
        }
    return promise.future()
}


/**
 * 获取频道身份组列表
 * @param channel 频道
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/26
 */
fun HttpAPIClient.getGuildRoles(
    channel: Channel,
    callback: ((GuildRolesBean) -> Unit)? = null,
): Future<GuildRolesBean> {
    val info = channel.botInfo
    val promise = promise<GuildRolesBean>()
    API.GuildRoles.addRestfulParam(channel.guildID).putHeaders(info.token.getHeaders()).send()
        .bodyJsonHandle(promise, "GuildRoles", "获取频道身份组失败") {
            if (!it.result) return@bodyJsonHandle
            it.body.toJsonObject().mapTo(GuildRolesBean::class.java).let { rolesBean ->
                if (rolesBean != null) {
                    rolesBean.channel = channel
                    promise.complete(rolesBean)
                    callback?.let { it1 -> it1(rolesBean) }
                } else {
                    val e = HttpClientException("The API returned unexpected content: ${it.body.writeToText()}")
                    logPreError(promise, "GuildRoles", "获取身分组信息失败", e).let { isLog ->
                        if (!promise.tryFail(e)) {
                            if (!isLog) apiError("GuildRoles", it.body.toJsonObject())
                        }
                    }
                }

            }
        }
    return promise.future()
}


/**
 * 获取频道身份组下的成员列表
 * @param channel 频道
 * @param roleID 身份组ID
 * @param after 上一次回包中最后一个member的user id， 如果是第一次请求填 0，默认为 0
 * @param limit 分页大小，成员较多的频道尽量使用较大的limit值，以减少请求数.使用 -1 则不分页
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/26
 */
private fun HttpAPIClient.getGuildRoleMembersPage(
    channel: Channel,
    roleID: String,
    after: String = "0",
    limit: Int = 400,
    callback: ((JsonObject) -> Unit)? = null,
): Future<JsonObject> {
    val info = channel.botInfo
    val promise = promise<JsonObject>()
    API.GuildRoleMembers.addRestfulParam(channel.guildID, roleID).putHeaders(info.token.getHeaders())
        .addQueryParam("start_index", after).addQueryParam("limit", limit.toString())
        .send().onSuccess { resp ->
            runCatching {
                val json = resp.bodyAsJsonObject()
                val data = json.getString("data")
                val code = json.getString("code")
                if (data != null && data.isNotEmpty()) {
                    promise.complete(json)
                    callback?.let { it1 -> it1(json) }
                } else if (code == "50001") { // {"code":50001,"message":"user not guild member"}
                    json.put("data", JsonArray())
                    promise.complete(json)
                    callback?.let { it1 -> it1(json) }
                } else {
                    val e = HttpClientException("The API returned unexpected content: ${resp.bodyAsString()}")
                    logPreError(promise, "GuildRoleMembersPage", "获取频道身份组成员失败", e).let { isLog ->
                        promise.tryFail(e).apply {
                            if (!this) apiError("GuildRoles", resp.bodyAsJsonObject())
                        }
                    }
                }
            }.onFailure {
                logPreError(promise, "GuildRoleMembersPage", "获取频道身份组成员失败", it).let { isLog ->
                    if (!promise.tryFail(it)) {
                        if (!isLog) logError("GuildRoleMembersPage", "获取频道身份组成员失败", it)
                    }
                }
            }
        }.onFailure {
            logPreError(promise, "GuildRoleMembersPage", "获取频道身份组成员失败", it).let { isLog ->
                if (!promise.tryFail(it)) {
                    if (!isLog) logError("GuildRoleMembersPage", "获取频道身份组成员失败", it)
                }
            }
        }
    return promise.future()
}


/**
 * 获取频道身份组下的成员列表
 * @param channel 频道
 * @param roleID 身份组ID
 * @param after 上一次回包中最后一个member的user id， 如果是第一次请求填 0，默认为 0
 * @param limit 分页大小，成员较多的频道尽量使用较大的limit值，以减少请求数.使用 -1 则不分页
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/26
 */
fun HttpAPIClient.getGuildRoleMembers(
    channel: Channel,
    roleID: String,
    after: String = "0",
    limit: Int = -1,
    callback: ((List<ChannelUser>) -> Unit)? = null,
): Future<List<ChannelUser>> {
    val list = HashSet<MemberBean>()
    var margin = limit
    val promise = promise<List<ChannelUser>>()
    var next = after
    task {
        kotlin.runCatching {
            while (margin != 0) {
                val temp = if (margin <= -1) 400 else margin
                val json = getGuildRoleMembersPage(channel, roleID, next, temp).await()
                val beans = json.getJsonArray("data").mapTo<MemberBean>()
                val tempNext = json.getString("next")
                beans.forEach {
                    if (margin == 0) return@forEach
                    list.add(it)
                    margin--
                }
                if (beans.isEmpty() || tempNext == null || tempNext.isEmpty() || tempNext == next) break
                next = tempNext
            }
        }.onFailure {
            logPreError(promise, "ChannelMembers", "获取频道成员失败", it).let { isLog ->
                if (!promise.tryFail(it)) {
                    if (!isLog) logError("ChannelMembers", "获取频道成员失败", it)
                }
            }
        }.onSuccess {
            val beans = list.toList().map { it.mapToChannelUser(channel) }
            promise.complete(beans)
            callback?.let { it1 -> it1(beans) }
        }
    }
    return promise.future()
}

