package com.github.zimoyin.qqbot.net.websocket.bean

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 *
 * @author : zimo
 * @date : 2023/12/26
 * 频道身份组列表
 */
data class GuildRolesBean(
    /**
     * 频道 ID
     */
    @field:JsonProperty("guild_id")
    val guildID: String = "0",

    /**
     * 一组频道身份组对象
     */
    val roles: List<RoleBean> = arrayListOf(),

    /**
     * 默认分组上限
     */
    @field:JsonProperty("role_num_limit")
    val roleLimit: String = "30",
) : Serializable

/**
 * 身份组对象
 */
data class RoleBean(
    /**
     * 身份组ID
     */
    @field:JsonProperty("id")
    val id: String = "",

    /**
     * 名称
     */
    @field:JsonProperty("name")
    val name: String = "",

    /**
     * ARGB的HEX十六进制颜色值转换后的十进制数值
     */
    @field:JsonProperty("color")
    val color: Long = 0,

    /**
     * 是否在成员列表中单独展示: 0-否, 1-是
     */
    @field:JsonProperty("hoist")
    val hoist: Int = 0,

    /**
     * 人数
     */
    @field:JsonProperty("number")
    val number: Int = -1,

    /**
     * 成员上限
     */
    @field:JsonProperty("member_limit")
    val memberLimit: Int = 30,
) : Serializable

enum class DefaultRoleIDs(val id: Int, val description: String) : Serializable {
    ALL_MEMBERS(1, "全体成员"),
    ADMIN(2, "管理员"),
    OWNER_CREATOR(4, "群主/创建者"),
    CHANNEL_ADMIN(5, "子频道管理员")
}