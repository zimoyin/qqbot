package io.github.zimoyin.qqbot.bot.contact.channel

import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.net.bean.*
import io.github.zimoyin.qqbot.net.bean.message.PinsMessageBean
import io.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.github.zimoyin.qqbot.net.http.api.channel.*
import io.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future

/**
 * 频道内容管理类，提供对频道公告、精华消息、日程、音频控制、机器人上/下麦、帖子等频道内容的管理功能。
 */
class AssetManagement(val channel: Channel) {
    /**
     * 创建频道公告
     * 公告类型分为 消息类型的频道公告 和 推荐子频道类型的频道公告
     * @param channel 频道
     * @param messageID 消息ID 信息ID请从频道消息中获取，使用他能创建一个 消息类型的频道公告。如果为 null 则 创建一个 推荐子频道类型的频道公告。
     * ;注意使用 该该数该信息ID要在该频道的ChannelID下存在
     * @param welcomeAnnouncement 是否是欢迎公告
     * @param recommend 推荐子频道
     */
    @OptIn(UntestedApi::class)
    fun createChannelAnnouncement(
        messageID: String? = null,
        welcomeAnnouncement: Boolean = false,
        recommend: List<RecommendChannelBean> = arrayListOf(),
    ): Future<AnnouncesBean> {
        return HttpAPIClient.createChannelAnnouncement(channel, messageID, welcomeAnnouncement, recommend)
    }


    /**
     * 删除频道公告
     * @param messageID 公告信息ID
     */
    @UntestedApi
    fun deleteChannelAnnouncement(messageID: String): Future<Boolean> {
        return HttpAPIClient.deleteChannelAnnouncement(channel, messageID)
    }

    /**
     * 删除所有频道公告
     */
    @OptIn(UntestedApi::class)
    fun deleteChannelAnnouncementAll(): Future<Boolean> {
        return deleteChannelAnnouncement("all")
    }

    /**
     * 添加精华消息
     */
    @OptIn(UntestedApi::class)
    fun addEssentialMessage(messageID: String): Future<PinsMessageBean> {
        if (!channel.isChannel) {
            val promise = promise<PinsMessageBean>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return HttpAPIClient.addEssentialMessage(channel, messageID)
    }

    /**
     * 删除精华消息
     */
    @OptIn(UntestedApi::class)
    fun deleteEssentialMessage(messageID: String): Future<Boolean> {
        if (!channel.isChannel) {
            val promise = promise<Boolean>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return HttpAPIClient.deleteEssentialMessage(channel, messageID)
    }


    /**
     * 获取精华消息
     */
    @OptIn(UntestedApi::class)
    fun getEssentialMessages(): Future<PinsMessageBean> {
        if (!channel.isChannel) {
            val promise = promise<PinsMessageBean>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return HttpAPIClient.getEssentialMessages(channel)
    }

    /**
     * 获取频道日程列表
     */
    @OptIn(UntestedApi::class)
    @JvmOverloads
    fun getChannelScheduleList(since: Long = System.currentTimeMillis()): Future<ScheduleBean> {
        if (!channel.isChannel) {
            val promise = promise<ScheduleBean>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return HttpAPIClient.getChannelScheduleList(channel, since)
    }

    /**
     * 获取日程详情
     */
    @OptIn(UntestedApi::class)
    fun getScheduleDetails(scheduleID: String): Future<ScheduleBean> {
        if (!channel.isChannel) {
            val promise = promise<ScheduleBean>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return HttpAPIClient.getScheduleDetails(channel, scheduleID)
    }

    /**
     * 创建日程
     */
    @OptIn(UntestedApi::class)
    fun createSchedule(schedule: ScheduleBean): Future<ScheduleBean> {
        return HttpAPIClient.createSchedule(channel, schedule)
    }

    /**
     * 修改日程
     */
    @OptIn(UntestedApi::class)
    fun updateSchedule(schedule: ScheduleBean, scheduleID: String): Future<ScheduleBean> {
        return HttpAPIClient.updateSchedule(channel, schedule, scheduleID)
    }

    /**
     * 删除日程
     */
    @OptIn(UntestedApi::class)
    fun deleteSchedule(scheduleID: String): Future<Boolean> {
        return HttpAPIClient.deleteSchedule(channel, scheduleID)
    }

    @OptIn(UntestedApi::class)
    fun audioControl() {
        //TODO 音频控制
        TODO("Not implemented")
    }

    /**
     * 机器人上麦
     */
    @OptIn(UntestedApi::class)
    fun robotOnStage(): Future<Boolean> {
        if (!channel.isChannel) {
            val promise = promise<Boolean>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return HttpAPIClient.robotOnStage(channel)
    }

    /**
     * 机器人下麦
     */
    @OptIn(UntestedApi::class)
    fun robotOffStage(): Future<Boolean> {
        if (!channel.isChannel) {
            val promise = promise<Boolean>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return HttpAPIClient.robotOffStage(channel)
    }


    /**
     * 获取帖子列表
     */
    @OptIn(UntestedApi::class)
    fun getPostList(): Future<List<ForumThread>> {
        if (!channel.isChannel) {
            val promise = promise<List<ForumThread>>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return HttpAPIClient.getPostList(channel)
    }

    /**
     * 获取帖子详情
     * @param id 帖子ID
     */
    @OptIn(UntestedApi::class)
    fun getPostDetails(id: String): Future<ForumThread> {
        if (!channel.isChannel) {
            val promise = promise<ForumThread>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return HttpAPIClient.getPostDetail(channel, id)
    }

    /**
     * 发表帖子
     * @param title 标题
     * @param content 内容
     * @param format 帖子格式
     */
    @OptIn(UntestedApi::class)
    @JvmOverloads
    fun publishPost(
        title: String,
        content: String,
        format: PostFormat = PostFormat.FORMAT_TEXT,
    ): Future<Boolean> {
        if (!channel.isChannel) {
            val promise = promise<Boolean>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return HttpAPIClient.publishPost(channel, title, content, format)
    }

    /**
     * 删除帖子
     * @param id 帖子ID
     */
    @OptIn(UntestedApi::class)
    fun deletePost(id: String): Future<Boolean> {
        if (!channel.isChannel) {
            val promise = promise<Boolean>()
            promise.fail(IllegalStateException("Channel is not a channel"))
            return promise.future()
        }
        return HttpAPIClient.deletePost(channel, id)
    }
}
