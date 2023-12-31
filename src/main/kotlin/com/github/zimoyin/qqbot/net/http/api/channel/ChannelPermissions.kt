package com.github.zimoyin.qqbot.net.http.api.channel

import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.net.bean.APIPermission
import com.github.zimoyin.qqbot.net.bean.APIPermissionDemand
import com.github.zimoyin.qqbot.net.bean.ContactPermission
import com.github.zimoyin.qqbot.net.http.addRestfulParam
import com.github.zimoyin.qqbot.net.http.api.API
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.utils.ex.promise
import com.github.zimoyin.qqbot.utils.ex.toJsonObject
import io.vertx.core.Future
import io.vertx.core.json.JsonObject


/**
 * 获取子频道用户权限
 *
 * @param channel 频道
 * @param userID 用户ID
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/31 031
 */
@UntestedApi
fun HttpAPIClient.getChannelPermissions(
  channel: Channel,
  userID: String,
  callback: ((ContactPermission) -> Unit)? = null,
): Future<ContactPermission> {
  val promise = promise<ContactPermission>()
  API.GetChannelPermissions
    .putHeaders(channel.botInfo.token.getHeaders())
    .addRestfulParam(channel.channelID!!, userID)
    .send()
    .onSuccess {
      kotlin.runCatching {
        val json = it.bodyAsJsonObject()
        val code = json.getInteger("code")
        val message = json.getString("message")
        if (code == null && message == null) {
          val permission = json.getString("permissions").toInt()
          val bean = ContactPermission(permission)
          promise.complete(bean)
          callback?.let { it1 -> it1(bean) }
        } else {
          logError(
            "getChannelPermissions",
            "result -> [$code] $message"
          )
        }
      }.onFailure {
        logError("getChannelPermissions", "获取用户权限失败", it)
        promise.fail(it)
      }
    }.onFailure {
      logError("getChannelPermissions", "获取用户权限失败", it)
      promise.fail(it)
    }
  return promise.future()
}


/**
 * 获取子频道机器人权限可用列表
 *
 * @param channel 频道
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/31 031
 */
fun HttpAPIClient.getChannelBotPermissions(
  channel: Channel,
  callback: ((List<APIPermission>) -> Unit)? = null,
): Future<List<APIPermission>> {
  val promise = promise<List<APIPermission>>()
  API.GetChannelBotPermissions
    .putHeaders(channel.botInfo.token.getHeaders())
    .addRestfulParam(channel.guildID)
    .send()
    .onSuccess {
      kotlin.runCatching {
        val json = it.bodyAsJsonObject()
        val code = json.getInteger("code")
        val message = json.getString("message")
        if (code == null && message == null) {
          val list = json.getJsonArray("apis").map {
            it.toJsonObject()
          }.map {
            it.mapTo(APIPermission::class.java).apply {
              channel0 = channel
            }
          }
          promise.complete(list)
          callback?.let { it1 -> it1(list) }
        } else {
          logError(
            "getChannelBotPermissions",
            "result -> [$code] $message"
          )
        }
      }.onFailure {
        logError("getChannelBotPermissions", "获取BOT权限列表失败", it)
        promise.fail(it)
      }
    }.onFailure {
      logError("getChannelBotPermissions", "获取BOT权限列表失败", it)
      promise.fail(it)
    }
  return promise.future()
}


/**
 * 获取子频道身份组权限
 *
 * @param channel 频道
 * @param roleID 身份组ID
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/31 031
 */
@UntestedApi
fun HttpAPIClient.getChannelRolePermissions(
  channel: Channel,
  roleID: String,
  callback: ((ContactPermission) -> Unit)? = null,
): Future<ContactPermission> {
  val promise = promise<ContactPermission>()
  API.GetChannelRolePermissions.putHeaders(channel.botInfo.token.getHeaders())
    .addRestfulParam(channel.channelID!!, roleID).send().onSuccess {
      kotlin.runCatching {
        val json = it.bodyAsJsonObject()
        val code = json.getInteger("code")
        val message = json.getString("message")
        if (code == null && message == null) {
          val permission = json.getString("permissions").toInt()
          val bean = ContactPermission(permission)
          promise.complete(bean)
          callback?.let { it1 -> it1(bean) }
        } else {
          logError(
            "getChannelRolePermissions",
            "result -> [$code] $message"
          )
        }
      }.onFailure {
        logError("getChannelRolePermissions", "获取子频道身份组权限失败", it)
        promise.fail(it)
      }
    }.onFailure {
      logError("getChannelRolePermissions", "获取子频道身份组权限失败", it)
      promise.fail(it)
    }
  return promise.future()
}


/**
 * 修改子频道用户权限
 * 该API的参数未研究明白，很大可能出现bug
 * @param channel 频道
 * @param userID 用户ID
 * @param permission 权限
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/31 031
 */
@UntestedApi
fun HttpAPIClient.updateChannelPermissions(
  channel: Channel,
  userID: String,
  permission: ContactPermission,
  callback: ((Boolean) -> Unit)? = null,
): Future<Boolean> {
  val promise = promise<Boolean>()
  val param = JsonObject().apply {
    put("add", permission.getPermission())
    put("remove", permission.getDifferenceSet().getPermission())
  }
  API.UpdateChannelPermissions.putHeaders(channel.botInfo.token.getHeaders())
    .addRestfulParam(channel.channelID!!, userID).sendJsonObject(param).onSuccess {
      kotlin.runCatching {
        if (it.statusCode() != 204) {
          val json = it.bodyAsJsonObject()
          val code = json.getInteger("code")
          val message = json.getString("message")
          if (code != null || message != null) {
            logError(
              "updateChannelPermissions",
              "result -> [$code] $message"
            )
          }
          promise.complete(false)
          callback?.let { it1 -> it1(false) }
        } else {
          promise.complete(true)
          callback?.let { it1 -> it1(true) }
        }
      }.onFailure {
        logError("updateChannelPermissions", "获取子频道身份组权限失败", it)
        promise.fail(it)
      }
    }.onFailure {
      logError("updateChannelPermissions", "获取子频道身份组权限失败", it)
      promise.fail(it)
    }
  return promise.future()
}


/**
 * 修改子频道身份组权限
 * 该API的参数未研究明白，很大可能出现bug
 * @param channel 频道
 * @param roleID 身份组ID
 * @param permission 权限
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/31 031
 */
@UntestedApi
fun HttpAPIClient.updateChannelRolePermissions(
  channel: Channel,
  roleID: String,
  permission: ContactPermission,
  callback: ((Boolean) -> Unit)? = null,
): Future<Boolean> {
  val promise = promise<Boolean>()
  val param = JsonObject().apply {
    put("add", permission.getPermission())
    put("remove", permission.getDifferenceSet().getPermission())
  }
  API.UpdateChannelRolePermissions.putHeaders(channel.botInfo.token.getHeaders())
    .addRestfulParam(channel.channelID!!, roleID).sendJsonObject(param).onSuccess {
      kotlin.runCatching {
        if (it.statusCode() != 204) {
          val json = it.bodyAsJsonObject()
          val code = json.getInteger("code")
          val message = json.getString("message")
          if (code != null || message != null) {
            logError(
              "updateChannelRolePermissions",
              "result -> [$code] $message"
            )
          }
          promise.complete(false)
          callback?.let { it1 -> it1(false) }
        } else {
          promise.complete(true)
          callback?.let { it1 -> it1(true) }
        }
      }.onFailure {
        logError("updateChannelRolePermissions", "修改子频道身分组权限失败", it)
        promise.fail(it)
      }
    }.onFailure {
      logError("updateChannelRolePermissions", "修改子频道身分组权限失败", it)
      promise.fail(it)
    }
  return promise.future()
}


/**
 * 请求频道Bot权限
 *
 * @param channel 频道
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/31 031
 */
@UntestedApi
fun HttpAPIClient.demandChannelBotPermissions(
  channel: Channel,
  permission: APIPermission,
  callback: ((APIPermissionDemand) -> Unit)? = null,
): Future<APIPermissionDemand> {
  val promise = promise<APIPermissionDemand>()
  API.DemandChannelBotPermissions
    .putHeaders(channel.botInfo.token.getHeaders())
    .addRestfulParam(channel.guildID)
    .sendJsonObject(permission.buildDemand())
    .onSuccess {
      kotlin.runCatching {
        val json = it.bodyAsJsonObject()
        val code = json.getInteger("code")
        val message = json.getString("message")
        if (code == null && message == null) {
          val list = json.mapTo(APIPermissionDemand::class.java)
          promise.complete(list)
          callback?.let { it1 -> it1(list) }
        } else {
          logError(
            "demandChannelBotPermissions",
            "result -> [$code] $message"
          )
        }
      }.onFailure {
        logError("demandChannelBotPermissions", "获取权限失败", it)
        promise.fail(it)
      }
    }.onFailure {
      logError("demandChannelBotPermissions", "获取权限失败", it)
      promise.fail(it)
    }
  return promise.future()
}

