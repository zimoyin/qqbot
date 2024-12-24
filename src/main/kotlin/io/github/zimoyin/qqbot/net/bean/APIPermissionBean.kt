package io.github.zimoyin.qqbot.net.bean

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.utils.JSON
import io.vertx.core.json.JsonObject
import java.io.Serializable

/**
 * API 接口权限对象
 */
data class APIPermission(
  /**
   * API 接口名，例如 /guilds/{guild_id}/members/{user_id}
   */
  @field:JsonProperty("path")
  val path: String? = null,

  /**
   * 请求方法，例如 GET
   */
  @field:JsonProperty("method")
  val method: String? = null,

  /**
   * API 接口名称，例如 获取频道信息
   */
  @field:JsonProperty("desc")
  val desc: String? = null,

  /**
   * 授权状态，auth_status 为 1 时已授权
   */
  @field:JsonProperty("auth_status")
  val authStatus: Int? = null,
) : Serializable {
  @field:JsonIgnore
  var channel0: Channel? = null

  /**
   * 构建权限需求对象
   */
  @JsonIgnore
  fun buildDemand(): JsonObject {
    return JSON.toJsonObject(
      """
      {
        "channel_id": "${channel0?.channelID}",
        "api_identify": {
          "path": "$path",
          "method": "$method"
        },
        "desc": "$desc"
      }
    """.trimIndent()
    )
  }
}

/**
 * API 接口权限需求对象
 */
data class APIPermissionDemand(
  /**
   * 申请接口权限的频道 id
   */
  @field:JsonProperty("guild_id")
  val guildID: String? = null,

  /**
   * 接口权限需求授权链接发送的子频道 id
   */
  @field:JsonProperty("channel_id")
  val channelID: String? = null,

  /**
   * 权限接口唯一标识
   */
  @field:JsonProperty("api_identify")
  val apiIdentify: APIPermissionDemandIdentify? = null,

  /**
   * 接口权限链接中的接口权限描述信息
   */
  @field:JsonProperty("title")
  val title: String? = null,

  /**
   * 接口权限链接中的机器人可使用功能的描述信息
   */
  @field:JsonProperty("desc")
  val description: String? = null,
) : Serializable

/**
 * API 接口权限需求标识对象
 */
data class APIPermissionDemandIdentify(
  /**
   * API 接口名，例如 /guilds/{guild_id}/members/{user_id}
   */
  @field:JsonProperty("path")
  val path: String? = null,

  /**
   * 请求方法，例如 GET
   */
  @field:JsonProperty("method")
  val method: String? = null,
) : Serializable
