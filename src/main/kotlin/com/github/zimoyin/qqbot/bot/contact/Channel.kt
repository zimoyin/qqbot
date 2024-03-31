package com.github.zimoyin.qqbot.bot.contact

import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.contact.channel.*
import com.github.zimoyin.qqbot.net.bean.ChannelBean
import com.github.zimoyin.qqbot.net.bean.GuildBean
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.net.http.api.channel.*
import com.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future

/**
 *
 * @author : zimo
 * @date : 2023/12/09
 *
 * 以后 频道 ID 不作为 联系人最先直观获取的ID 而是称为父亲ID，如果没有就是他本身
 */
interface Channel : Contact {
    /**
     * 频道ID 与 channelID 一致。如果没有则是 guildID
     */
    override val id: String

    /**
     * 频道ID
     */
    val guildID: String

    /**
     * 子频道ID
     */
    val channelID: String?

    /**
     * 如果是私信就是 srcGuildID(临时ID) 否则就是 channelID （为空就是 guildID）
     */
    val currentID: String

    /**
     * 是否是频道
     */
    val isGuild: Boolean
        get() = channelID == null || channelID != guildID

    /**
     * 是否是子频道
     */
    val isChannel: Boolean
        get() = channelID != null && channelID != guildID


    /**
     * 是否是私聊
     */
    val isPrivateChat: Boolean
        get() = currentID.isNotEmpty() && channelID != currentID && currentID != guildID

    /**
     * 撤回消息
     * @param messageID 消息ID
     */
    override fun recall(messageID: String): Future<Boolean> {
        return recall(messageID, false)
    }

    /**
     * 撤回消息
     * @param messageID 消息ID
     * @param hide 是否隐藏撤回信息的小灰条提示
     */
    @OptIn(UntestedApi::class)
    fun recall(messageID: String, hide: Boolean = true): Future<Boolean> {
        val promise = promise<Boolean>()

        // 撤回频道私聊内的信息
        if (isPrivateChat) HttpAPIClient.recallChannelPrivateMessage(this, messageID, false).onSuccess {
            promise.tryComplete(it)
        }.onFailure {
            promise.fail(it)
        }
        // 撤回通道内的信息
        else HttpAPIClient.recallChannelMessage(this, messageID, hide).onSuccess {
            promise.complete(it)
        }.onFailure {
            promise.fail(it)
        }

        return promise.future()
    }

    /**
     * 获取频道详情
     */
    fun getGuildDetails(): Future<GuildBean> {
        return HttpAPIClient.getGuildDetails(channel = this)
    }

    /**
     * 获取子频道详情
     */
    fun getChannelDetails(): Future<ChannelBean> {
        require(isChannel) { "This channel not is sub channel" }
        return HttpAPIClient.getChannelDetails(this)
    }

    /**
     * 获取子频道列表
     */
    fun getChannels(): Future<List<Channel>> {
        return HttpAPIClient.getChannels(this)
    }

    /**
     * 获取子频道列表信息
     * 该方法区别于 getChannels() 该方法只返回子频道信息
     */
    fun getChannelInfos(): Future<List<ChannelBean>> {
        return HttpAPIClient.getChannelInfos(this)
    }

    /**
     * 获取频道成员列表
     *
     */
    fun getGuildMembers(): Future<List<ChannelUser>> {
        return HttpAPIClient.getGuildMembers(this)
    }

    /**
     * 获取子频道在线成员数
     * 注意: 用于查询音视频/直播子频道 channel_id 的在线成员数
     */
    fun getLiveChannelOnlineTotal(): Future<Int> {
        require(isChannel) { "This channel not is sub channel" }
        return HttpAPIClient.getChannelOnlineMemberSize(this)
    }

    /**
     * 获取某个具体的子频道
     */
    fun getChannel(subChannelID: String): Future<Channel?> {
        val promise = promise<Channel?>()
        getChannels().onSuccess {
            val channel = it.firstOrNull { subChannelID == it.channelID }
            promise.complete(channel)
        }.onFailure {
            promise.fail(it)
        }
        return promise.future()
    }

    /**
     * 子频道管理
     */
    val channelOperate: ChannelOperate
        get() = ChannelOperate(this)

    /**
     * 频道资料/内容管理
     */
    val assetManagement: AssetManagement
        get() = AssetManagement(this)

    /**
     * 频道身份组管理
     */
    val guildRoles: GuildRoles
        get() = GuildRoles(this)

    /**
     * 频道权限管理
     */
    val authorization: Authorization
        get() = Authorization(this)

    /**
     * 频道禁言管理
     */
    val mute: Mute
        get() = Mute(this)

    //TODO 小程序相关
}

