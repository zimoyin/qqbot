package io.github.zimoyin.qqbot.bot.contact.channel

import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.Role
import io.github.zimoyin.qqbot.bot.contact.User
import io.github.zimoyin.qqbot.net.bean.APIPermission
import io.github.zimoyin.qqbot.net.bean.APIPermissionDemand
import io.github.zimoyin.qqbot.net.bean.ContactPermission
import io.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.github.zimoyin.qqbot.net.http.api.channel.*
import io.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future

/**
 * 权限管理类，提供对子频道用户权限、身份组权限以及机器人在频道接口权限的获取、修改和授权链接发送等功能。
 */
class Authorization(val channel: Channel) {
    /**
     * 获取子频道用户权限
     */
    @OptIn(UntestedApi::class)
    fun getChannelPermissions(user: User): Future<ContactPermission> {
        if (!channel.isChannel) {
            val promise = promise<ContactPermission>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return HttpAPIClient.getChannelPermissions(channel, user.id)
    }

    /**
     * 获取子频道用户权限
     */
    @OptIn(UntestedApi::class)
    fun getChannelPermissions(userID: String): Future<ContactPermission> {
        if (!channel.isChannel) {
            val promise = promise<ContactPermission>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return HttpAPIClient.getChannelPermissions(channel, userID)
    }

    /**
     * 修改子频道用户权限
     */
    @OptIn(UntestedApi::class)
    fun updateChannelPermissions(user: User, permissions: ContactPermission): Future<Boolean> {
        if (!channel.isChannel) {
            val promise = promise<Boolean>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return HttpAPIClient.updateChannelPermissions(channel, user.id, permissions)
    }

    /**
     * 修改子频道用户权限
     */
    @OptIn(UntestedApi::class)
    fun updateChannelPermissions(userID: String, permissions: ContactPermission): Future<Boolean> {
        if (!channel.isChannel) {
            val promise = promise<Boolean>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return HttpAPIClient.updateChannelPermissions(channel, userID, permissions)
    }

    /**
     * 获取子频道身份组权限
     */
    @OptIn(UntestedApi::class)
    fun getChannelRolePermissions(role: Role): Future<ContactPermission> {
        if (!channel.isChannel) {
            val promise = promise<ContactPermission>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return HttpAPIClient.getChannelRolePermissions(channel, role.id)
    }

    /**
     * 获取子频道身份组权限
     */
    @OptIn(UntestedApi::class)
    fun getChannelRolePermissions(roleID: String): Future<ContactPermission> {
        if (!channel.isChannel) {
            val promise = promise<ContactPermission>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return HttpAPIClient.getChannelRolePermissions(channel, roleID)
    }

    /**
     * 修改子频道身份组权限
     */
    @OptIn(UntestedApi::class)
    fun updateChannelRolePermissions(role: Role, permissions: ContactPermission): Future<Boolean> {
        if (!channel.isChannel) {
            val promise = promise<Boolean>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return HttpAPIClient.updateChannelRolePermissions(channel, role.id, permissions)
    }

    /**
     * 修改子频道身份组权限
     */
    @OptIn(UntestedApi::class)
    fun updateChannelRolePermissions(roleID: String, permissions: ContactPermission): Future<Boolean> {
        if (!channel.isChannel) {
            val promise = promise<Boolean>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return HttpAPIClient.updateChannelRolePermissions(channel, roleID, permissions)
    }

    /**
     * 获取机器人在频道可用权限列表
     */
    fun getBotPermissions(): Future<List<APIPermission>> {
        return HttpAPIClient.getChannelBotPermissions(channel)
    }

    /**
     * 发送机器人在频道接口权限的授权链接
     */
    @OptIn(UntestedApi::class)
    fun sendAuthLink(permissions: APIPermission): Future<APIPermissionDemand> {
        return HttpAPIClient.demandChannelBotPermissions(channel, permissions)
    }
}
