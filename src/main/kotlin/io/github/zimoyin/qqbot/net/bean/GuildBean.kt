package io.github.zimoyin.qqbot.net.bean

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 * 频道对象
 *
 * 频道对象中所涉及的 ID 类数据，都仅在机器人场景流通，与真实的 ID 无关。请不要理解为真实的 ID
 */
data class GuildBean(
    /**
     * 频道ID
     */
    @field:JsonProperty("id")
    val id: String? = null,

    /**
     * 频道名称
     */
    @field:JsonProperty("name")
    val name: String? = null,

    @field:JsonProperty("union_org_id")
    val unionOrgId: String? = null,

    @field:JsonProperty("union_world_id")
    val unionWorldId: String? = null,

    /**
     * 频道头像地址
     */
    @field:JsonProperty("icon")
    val icon: String? = null,

    /**
     * 创建人用户ID
     */
    @field:JsonProperty("owner_id")
    val ownerId: String? = null,

    /**
     * 当前人是否是创建人
     */
    @field:JsonProperty("owner")
    val isOwner: Boolean? = null,

    /**
     * 成员数
     */
    @field:JsonProperty("member_count")
    val memberCount: Int? = null,

    /**
     * 最大成员数
     */
    @field:JsonProperty("max_members")
    val maxMembers: Int? = null,

    /**
     * 描述
     */
    @field:JsonProperty("description")
    val description: String? = null,

    /**
     * 加入时间
     */
    @field:JsonProperty("joined_at")
    val joinedAt: String? = null,


    /**
     * 操作者ID，通常用于频道管理
     */
    @field:JsonProperty("op_user_id")
    val opUserId: String? = null,


    /**
     * 操作者ID，通常用于频道管理
     */
    @field:JsonProperty("union_appid")
    val unionAppID: String? = null,


) : Serializable
