package com.github.zimoyin.qqbot.bot.contact.channel

import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.bot.contact.User
import com.github.zimoyin.qqbot.net.bean.MemberBean
import com.github.zimoyin.qqbot.net.bean.MessageSetting
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.net.http.api.channel.getChannelRate
import com.github.zimoyin.qqbot.net.http.api.channel.setChannelMute
import com.github.zimoyin.qqbot.net.http.api.channel.setChannelMuteMember
import com.github.zimoyin.qqbot.net.http.api.channel.setChannelMuteMembers
import io.vertx.core.Future

/**
 * 禁言
 */
class Mute(val channel: Channel) {
    /**
     * 获取频道消息频率的设置详情
     */
    @UntestedApi
    fun getChannelRate(): Future<MessageSetting> {
        return HttpAPIClient.getChannelRate(channel)
    }

    /**
     * 频道全员禁言，包括子频道
     */
    @UntestedApi
    @JvmOverloads
    fun setChannelMute(
        muteTimestamp: Long = 24 * 60 * 1000,
        muteEndTimestamp: Long = System.currentTimeMillis() + muteTimestamp,
    ): Future<Boolean> {
        return HttpAPIClient.setChannelMute(channel, muteTimestamp, muteEndTimestamp)
    }

    /**
     * 频道指定成员禁言
     * 该接口同样支持解除指定成员禁言，将mute_end_timestamp或mute_seconds传值为字符串'0'即可
     */
    @UntestedApi
    @JvmOverloads
    fun setChannelMuteMember(
        userID: String,
        muteTimestamp: Long = 24 * 60 * 1000,
        muteEndTimestamp: Long = System.currentTimeMillis() + muteTimestamp,
    ): Future<Boolean> {
        return HttpAPIClient.setChannelMuteMember(channel, userID, muteTimestamp, muteEndTimestamp)
    }

    /**
     * 频道指定成员禁言
     * 该接口同样支持解除指定成员禁言，将mute_end_timestamp或mute_seconds传值为字符串'0'即可
     */
    @UntestedApi
    @JvmOverloads
    fun setChannelMuteMember(
        user: User,
        muteTimestamp: Long = 24 * 60 * 1000,
        muteEndTimestamp: Long = System.currentTimeMillis() + muteTimestamp,
    ): Future<Boolean> {
        return setChannelMuteMember(user.id, muteTimestamp, muteEndTimestamp)
    }


    /**
     * 频道批量成员禁言
     */
    @UntestedApi
    @JvmOverloads
    fun setChannelMuteMembers(
        vararg users: MemberBean,
        muteTimestamp: Long = 24 * 60 * 1000,
        muteEndTimestamp: Long = System.currentTimeMillis() + muteTimestamp,
    ): Future<Boolean> {
        return HttpAPIClient.setChannelMuteMembers(
            channel,
            users.map { it.user!!.uid },
            muteTimestamp,
            muteEndTimestamp
        )
    }

    /**
     * 频道批量成员禁言
     */
    @UntestedApi
    @JvmOverloads
    fun setChannelMuteMembers(
        vararg userID: String,
        muteTimestamp: Long = 24 * 60 * 1000,
        muteEndTimestamp: Long = System.currentTimeMillis() + muteTimestamp,
    ): Future<Boolean> {
        return HttpAPIClient.setChannelMuteMembers(channel, userID.toList(), muteTimestamp, muteEndTimestamp)
    }
}
