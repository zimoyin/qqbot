package com.github.zimoyin.qqbot.bot.contact.channel

import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.bot.contact.User
import com.github.zimoyin.qqbot.net.bean.ChannelBean
import com.github.zimoyin.qqbot.net.bean.PrivateType
import com.github.zimoyin.qqbot.net.bean.SpeakPermission
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.net.http.api.channel.*
import com.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future

/**
 * 子频道管理类，提供创建、更新、删除子频道以及删除频道成员等功能。
 *
 */
class ChannelOperate(val channel: Channel) {

    /**
     * 创建子频道
     * @param subChannel 子频道信息
     * 请使用 ChannelBean.crate(...) 进行创建
     *
     * @return 子频道信息
     */
    @UntestedApi
    fun createChannel(subChannel: ChannelBean): Future<Channel> {
        return HttpAPIClient.creatSubChannel(channel, subChannel)
    }

    /**
     * 更新子频道
     * 需要修改哪个字段，就传递哪个字段即可。
     * @param name 子频道名称
     * @param position 子频道排序位置
     * @param parentId 父频道ID
     * @param privateType 子频道私密性
     * @param speakPermission 子频道发言权限
     */
    @UntestedApi
    @JvmOverloads
    fun updateChannel(
        name: String? = null,
        position: Int? = null,
        parentId: String? = null,
        privateType: PrivateType? = null,
        speakPermission: SpeakPermission? = null,
    ): Future<Channel> {
        if (!channel.isChannel){
            val promise = promise<Channel>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return HttpAPIClient.updateSubChannel(
            channel, name, position, parentId, privateType?.value, speakPermission?.value
        )
    }

    /**
     * 删除子频道
     */
    @OptIn(UntestedApi::class)
    fun deleteChannel(): Future<Boolean> {
        if (!channel.isChannel){
            val promise = promise<Boolean>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return HttpAPIClient.deleteSubChannel(channel)
    }


    /**
     * 删除频道成员
     * @param userID 成员ID
     * @param addBlacklist 是否将用户加入黑名单
     * @param deleteHistoryMsg 是否删除用户的历史消息
     */
    @UntestedApi
    @JvmOverloads
    fun deleteGuildMember(
        userID: String,
        addBlacklist: Boolean = false,
        deleteHistoryMsg: MessageRevokeTimeRange = MessageRevokeTimeRange.NO_REVOKE,
    ): Future<Boolean> {
        return HttpAPIClient.deleteSubChannelMember(channel, userID, addBlacklist, deleteHistoryMsg)
    }

    /**
     * 删除频道成员
     * @param userID 成员ID
     * @param addBlacklist 是否将用户加入黑名单
     * @param deleteHistoryMsg 是否删除用户的历史消息
     */
    @UntestedApi
    @JvmOverloads
    fun deleteGuildMember(
        userID: User,
        addBlacklist: Boolean = false,
        deleteHistoryMsg: MessageRevokeTimeRange = MessageRevokeTimeRange.NO_REVOKE,
    ): Future<Boolean> {
        return HttpAPIClient.deleteSubChannelMember(channel, userID.id, addBlacklist, deleteHistoryMsg)
    }
}
