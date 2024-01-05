package com.github.zimoyin.qqbot.bot.contact.channel

import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.bot.contact.ChannelUser
import com.github.zimoyin.qqbot.bot.contact.Role
import com.github.zimoyin.qqbot.bot.contact.User
import com.github.zimoyin.qqbot.net.bean.GuildRolesBean
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.net.http.api.channel.*
import com.github.zimoyin.qqbot.utils.Color
import com.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future

/**
 * 身份组管理类，提供获取、创建、更新、删除身份组以及管理身份组成员等功能。
 */
class GuildRoles(val channel: Channel) {
    /**
     * 获取频道身份组列表
     */
    fun getGuildRoles(): Future<GuildRolesBean> {
        return HttpAPIClient.getGuildRoles(channel)
    }

    /**
     * 获取频道身份组成员列表
     */
    fun getGuildRoleMembers(roleID: String): Future<List<ChannelUser>> {
        return HttpAPIClient.getGuildRoleMembers(channel, roleID)
    }

    /**
     * 创建频道身份组
     * @param name 身份组名称
     * @param color 身份组颜色
     * @param hoist 是否在成员列表中显示
     */
    @UntestedApi
    @JvmOverloads
    fun createGuildRole(
        name: String = "0",
        color: Int = Color.TRANSPARENT_BLACK.toArgb(),
        hoist: Boolean = true,
    ): Future<Role> {
        return HttpAPIClient.createGuildRole(channel, name, color, hoist)
    }

    /**
     * 修改频道身份组
     * @param roleID 身份组ID
     * @param name 身份组名称
     * @param color 身份组颜色
     * @param hoist 是否在成员列表中显示
     *
     * 接口会修改传入的字段，不传入的默认不会修改，至少要传入一个参数。
     */
    @UntestedApi
    @JvmOverloads
    fun updateGuildRole(
        roleID: String,
        name: String? = null,
        color: Int? = null,
        hoist: Boolean? = null,
    ): Future<Role> {
        return HttpAPIClient.updateGuildRole(channel, roleID, name, color, hoist)
    }


    /**
     * 删除频道身份组
     * @param roleID 身份组ID
     */
    @UntestedApi
    fun deleteGuildRole(roleID: String): Future<Boolean> {
        return HttpAPIClient.deleteGuildRole(channel, roleID)
    }

    /**
     * 创建频道身份组成员
     * 用于将频道guild_id下的用户 user_id 添加到身份组 role_id
     */
    @UntestedApi
    fun addGuildRoleMember(user: User, roleID: String): Future<Role> {
        if (!channel.isChannel){
            val promise = promise<Role>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return addGuildRoleMember(user.id, roleID)
    }

    /**
     * 创建频道身份组成员
     * 用于将频道guild_id下的用户 user_id 添加到身份组 role_id
     */
    @UntestedApi
    fun addGuildRoleMember(userID: String, roleID: String): Future<Role> {
        if (!channel.isChannel){
            val promise = promise<Role>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return HttpAPIClient.addGuildRoleMember(channel, userID, roleID)
    }

    /**
     * 删除频道身份组成员
     */
    @UntestedApi
    fun deleteGuildRoleMember(userID: User, roleID: String): Future<Role> {
        if (!channel.isChannel){
            val promise = promise<Role>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return deleteGuildRoleMember(userID.id, roleID)
    }

    /**
     * 删除频道身份组成员
     */
    @UntestedApi
    fun deleteGuildRoleMember(userID: String, roleID: String): Future<Role> {
        if (!channel.isChannel){
            val promise = promise<Role>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return HttpAPIClient.deleteGuildRoleMember(channel, userID, roleID)
    }
}
