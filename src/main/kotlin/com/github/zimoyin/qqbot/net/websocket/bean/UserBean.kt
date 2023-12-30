package com.github.zimoyin.qqbot.net.websocket.bean

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.time.Instant

/**
 *
 * @author : zimo
 * @date : 2023/12/11
 */


/**
 * 用户对象
 */
data class User(
    /**
     * 在频道中的用户 id
     */
    @field:JsonProperty("id")
    val inChannelUserID: String? = null,
    /**
     * 在群聊中的用户 id
     */
    @field:JsonProperty("member_openid")
    val inGroupUserID: String? = null,
    /**
     * 在单独的用户聊天中的用户 id
     */
    @field:JsonProperty("user_openid")
    val inOpenUserID: String? = null,

    /**
     * 用户名
     */
    @field:JsonProperty("username")
    val username: String? = null,

    /**
     * 用户头像地址
     */
    @field:JsonProperty("avatar")
    val avatar: String? = null,

    /**
     * 是否是机器人
     */
    @field:JsonProperty("bot")
    val isBot: Boolean? = null,

    /**
     * 特殊关联应用的 openid
     * 需要特殊申请并配置后才会返回
     */
    @field:JsonProperty("union_openid")
    val unionOpenID: String? = null,

    /**
     * 机器人关联的互联应用的用户信息
     * 与 union_openid 关联的应用是同一个
     */
    @field:JsonProperty("union_user_account")
    val unionUserAccount: String? = null,
) : Serializable {
    /**
     * 获取ID，在哪个场景下就获取哪个ID
     */
    val uid: String by lazy {
        inChannelUserID ?: inGroupUserID ?: inOpenUserID ?: throw NullPointerException("user ID is null")
    }
}


/**
 * 成员对象
 */
data class MemberBean(
    /**
     * 用户的频道基础信息
     * 只有成员相关接口中会填充此信息
     */
    @field:JsonProperty("user")
    val user: User? = null,

    /**
     * 用户的昵称
     */
    @field:JsonProperty("nick")
    val nick: String? = null,

    /**
     * 用户在频道内的身份组ID, 默认值可参考DefaultRoles
     */
    @field:JsonProperty("roles")
    val roles: List<String>? = null,

    /**
     * 用户加入频道的时间
     */
    @field:JsonProperty("joined_at")
    val joinedAt: Instant? = null,
) : Serializable

/**
 * 带有频道id的成员对象
 */
data class MemberWithGuildID(
    /**
     * 频道id
     */
    @field:JsonProperty("guild_id")
    val guildID: String? = null,

    /**
     * 用户的频道基础信息
     */
    @field:JsonProperty("user")
    val user: User? = null,

    /**
     * 用户的昵称
     */
    @field:JsonProperty("nick")
    val nick: String? = null,

    /**
     * 用户在频道内的身份
     */
    @field:JsonProperty("roles")
    val roles: List<String>? = null,

    /**
     * 用户加入频道的时间
     */
    @field:JsonProperty("joined_at")
    val joinedAt: Instant? = null,

    /**
     * 操作人ID
     * 当事件发生时该字段才会填充
     */
    @field:JsonProperty("op_user_id")
    val opUserID:String? = null,
) : Serializable


@JsonIgnoreProperties(ignoreUnknown = true)
data class BotUser(
    val id: String = "",
    val username: String = "",
    val bot: Boolean = true,
    val status: Int = 0,
    val avatar: String? = null,
    @field:JsonProperty("union_openid")
    val unionOpenID: String? = null,
    @field:JsonProperty("union_user_account")
    val unionUserAccount: String? = null,
) : Serializable