package io.github.zimoyin.qqbot.net.bean

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.github.zimoyin.qqbot.net.http.api.channel.deletePost
import io.github.zimoyin.qqbot.net.http.api.channel.getPostDetail
import io.vertx.core.Future
import java.io.Serializable

// ForumThread 类
data class ForumThread(
    /**
     * 频道ID
     */
    @field:JsonProperty("guild_id")
    val guildId: String? = null,

    /**
     * 子频道ID
     */
    @field:JsonProperty("channel_id")
    val channelId: String? = null,

    /**
     * 作者ID
     */
    @field:JsonProperty("author_id")
    val authorId: String? = null,

    /**
     * 主帖内容
     */
    @field:JsonProperty("thread_info")
    val threadInfo: ThreadInfo? = null,
) : Serializable {

    @field:JsonIgnore
    var channel: Channel? = null

    /**
     * 获取帖子详情
     */
    @OptIn(UntestedApi::class)
    fun getPostDetails(): Future<ForumThread> {
        return HttpAPIClient.getPostDetail(channel!!, threadInfo!!.threadId!!)
    }

    /**
     * 删除帖子
     */
    @OptIn(UntestedApi::class)
    fun deletePost(): Future<Boolean> {
        return HttpAPIClient.deletePost(channel!!, threadInfo!!.threadId!!)
    }
}

// ThreadInfo 类
data class ThreadInfo(
    /**
     * 主帖ID
     */
    @field:JsonProperty("thread_id")
    val threadId: String? = null,

    /**
     * 帖子标题
     */
    val title: String? = null,

    /**
     * 帖子内容
     */
    val content: String? = null,

    /**
     * 发表时间
     */
    @field:JsonProperty("date_time")
    val dateTime: String? = null,
) : Serializable


// ForumPost 类
data class ForumPost(
    /**
     * 频道ID
     */
    @field:JsonProperty("guild_id")
    val guildId: String? = null,

    /**
     * 子频道ID
     */
    @field:JsonProperty("channel_id")
    val channelId: String? = null,

    /**
     * 作者ID
     */
    @field:JsonProperty("author_id")
    val authorId: String? = null,

    /**
     * 帖子内容
     */
    @field:JsonProperty("post_info")
    val postInfo: PostInfo? = null,
) : Serializable

// PostInfo 类
data class PostInfo(
    /**
     * 主题ID
     */
    @field:JsonProperty("thread_id")
    val threadId: String? = null,

    /**
     * 帖子ID
     */
    @field:JsonProperty("post_id")
    val postId: String? = null,

    /**
     * 帖子内容
     */
    val content: String? = null,

    /**
     * 评论时间
     */
    @field:JsonProperty("date_time")
    val dateTime: String? = null,
) : Serializable

// ForumReply 类
data class ForumReply(
    /**
     * 频道ID
     */
    @field:JsonProperty("guild_id")
    val guildId: String? = null,

    /**
     * 子频道ID
     */
    @field:JsonProperty("channel_id")
    val channelId: String? = null,

    /**
     * 作者ID
     */
    @field:JsonProperty("author_id")
    val authorId: String? = null,

    /**
     * 回复内容
     */
    @field:JsonProperty("reply_info")
    val replyInfo: ReplyInfo? = null,
) : Serializable

// ReplyInfo 类
data class ReplyInfo(
    /**
     * 主题ID
     */
    @field:JsonProperty("thread_id")
    val threadId: String? = null,

    /**
     * 帖子ID
     */
    @field:JsonProperty("post_id")
    val postId: String? = null,

    /**
     * 回复ID
     */
    @field:JsonProperty("reply_id")
    val replyId: String? = null,

    /**
     * 回复内容
     */
    val content: String? = null,

    /**
     * 回复时间
     */
    @field:JsonProperty("date_time")
    val dateTime: String? = null,
) : Serializable


// ForumAuditResult 类
data class ForumAuditResult(
    /**
     * 频道ID
     */
    @field:JsonProperty("guild_id")
    val guildId: String? = null,

    /**
     * 子频道ID
     */
    @field:JsonProperty("channel_id")
    val channelId: String? = null,

    /**
     * 作者ID
     */
    @field:JsonProperty("author_id")
    val authorId: String? = null,

    /**
     * 主题ID
     */
    @field:JsonProperty("thread_id")
    val threadId: String? = null,

    /**
     * 帖子ID
     */
    @field:JsonProperty("post_id")
    val postId: String? = null,

    /**
     * 回复ID
     */
    @field:JsonProperty("reply_id")
    val replyId: String? = null,

    /**
     * 审核的类型
     */
    val type: UInt? = null,

    /**
     * 审核结果. 0:成功 1:失败
     */
    val result: UInt? = null,

    /**
     * result不为0时错误信息
     */
    @field:JsonProperty("err_msg")
    val errMsg: String? = null,
) : Serializable

// ForumAuditType 类
data class ForumAuditType(
    /**
     * 审核的类型
     */
    val type: UInt? = null,

    /**
     * 描述
     */
    val description: String? = null,
) : Serializable
