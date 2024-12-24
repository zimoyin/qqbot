package io.github.zimoyin.qqbot.net.http.api.channel

import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.Role
import io.github.zimoyin.qqbot.net.bean.RoleBean
import io.github.zimoyin.qqbot.net.http.addRestfulParam
import io.github.zimoyin.qqbot.net.http.api.API
import io.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.github.zimoyin.qqbot.utils.Color
import io.github.zimoyin.qqbot.utils.ex.promise
import io.github.zimoyin.qqbot.utils.ex.toInt
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.jsonObjectOf

/**
 * 创建频道身份组
 *
 * @param channel 频道
 * @param name 身份组名称
 * @param color 身份组颜色
 * @param hoist 是否在成员列表中显示
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/28 028
 */
@UntestedApi
fun HttpAPIClient.createGuildRole(
    channel: Channel,
    name: String = "0",
    color: Int = Color.TRANSPARENT_BLACK.toArgb(),
    hoist: Boolean = true,
    callback: ((Role) -> Unit)? = null,
): Future<Role> {
    val promise = promise<Role>()
    val param = jsonObjectOf("name" to name, "color" to color, "hoist" to hoist.toInt())
    API.CreateGuildRole
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.guildID)
        .sendJsonObject(param)
        .bodyJsonHandle(promise, "CreateGuildRole", "创建频道身份组失败") {
            if (!it.result) return@bodyJsonHandle
            val role = it.body.toJsonObject().getJsonObject("role").mapTo(RoleBean::class.java).mapToRole(channel)
            promise.complete(role)
            callback?.let { it1 -> it1(role) }
        }
    return promise.future()
}


/**
 * 修改频道身份组
 *
 * @param channel 频道
 * @param name 身份组名称
 * @param color 身份组颜色
 * @param hoist 是否在成员列表中显示
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/28 028
 */
@UntestedApi
fun HttpAPIClient.updateGuildRole(
    channel: Channel,
    roleID: String,
    name: String?,
    color: Int?,
    hoist: Boolean?,
    callback: ((Role) -> Unit)? = null,
): Future<Role> {
    val promise = promise<Role>()
    val param = JsonObject().apply {
        name?.let { put("name", name) }
        color?.let { put("color", color) }
        hoist?.let { put("hoist", hoist) }
    }
    API.UpdateGuildRole
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.guildID, roleID)
        .sendJsonObject(param)
        .bodyJsonHandle(promise, "UpdateGuildRole", "修改频道身份组失败") {
            if (!it.result) return@bodyJsonHandle
            val role = it.body.toJsonObject().getJsonObject("role").mapTo(RoleBean::class.java).mapToRole(channel)
            promise.complete(role)
            callback?.let { it1 -> it1(role) }
        }
    return promise.future()
}

/**
 * 删除频道身份组
 *
 * @param channel 频道
 * @param name 身份组名称
 * @param color 身份组颜色
 * @param hoist 是否在成员列表中显示
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/28 028
 */
@UntestedApi
fun HttpAPIClient.deleteGuildRole(
    channel: Channel,
    roleID: String,
    callback: ((Boolean) -> Unit)? = null,
): Future<Boolean> {
    val promise = promise<Boolean>()
    API.UpdateGuildRole
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.guildID, roleID)
        .send()
        .bodyJsonHandle(promise, "DeleteGuildRole", "删除频道身份组失败") {
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
 * 创建频道身份组成员
 *
 * @param channel 频道
 * @param name 身份组名称
 * @param color 身份组颜色
 * @param hoist 是否在成员列表中显示
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/28 028
 */
@UntestedApi
fun HttpAPIClient.addGuildRoleMember(
    channel: Channel,
    userID: String,
    roleID: String,
    callback: ((Role) -> Unit)? = null,
): Future<Role> {
    val promise = promise<Role>()
    val param = JsonObject().apply {
//        put("channel", jsonObjectOf("id" to channel.guildID))
        //不确定是上面还是下面的语句哪个有效
        put("channel", jsonObjectOf("id" to channel.channelID))
    }
    API.AddGuildRoleMember
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.guildID, userID, roleID)
        .sendJsonObject(param)
        .bodyJsonHandle(promise, "AddGuildRoleMember", "添加频道身份组成员失败") {
            if (!it.result) return@bodyJsonHandle
            val role = it.body.toJsonObject().getJsonObject("role").mapTo(RoleBean::class.java).mapToRole(channel)
            promise.complete(role)
            callback?.let { it1 -> it1(role) }
        }
    return promise.future()
}


/**
 * 删除频道身份组成员
 *
 * @param channel 频道
 * @param name 身份组名称
 * @param color 身份组颜色
 * @param hoist 是否在成员列表中显示
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/28 028
 */
@UntestedApi
fun HttpAPIClient.deleteGuildRoleMember(
    channel: Channel,
    userID: String,
    roleID: String,
    callback: ((Role) -> Unit)? = null,
): Future<Role> {
    val promise = promise<Role>()
    val param = JsonObject().apply {
//        put("channel", jsonObjectOf("id" to channel.guildID))
        //不确定是上面还是下面的语句哪个有效
        put("channel", jsonObjectOf("id" to channel.channelID))
    }
    API.DeleteGuildRoleMember
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.guildID, userID, roleID)
        .sendJsonObject(param)
        .bodyJsonHandle(promise, "DeleteGuildRoleMember", "删除频道身份组成员失败"){
            if (!it.result) return@bodyJsonHandle
            val role = it.body.toJsonObject().getJsonObject("role").mapTo(RoleBean::class.java).mapToRole(channel)
            promise.complete(role)
            callback?.let { it1 -> it1(role) }
        }
    return promise.future()
}
