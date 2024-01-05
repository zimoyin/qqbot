package com.github.zimoyin.qqbot.net.http.api.channel

import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.net.bean.ForumThread
import com.github.zimoyin.qqbot.net.http.addRestfulParam
import com.github.zimoyin.qqbot.net.http.api.API
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.utils.ex.promise
import com.github.zimoyin.qqbot.utils.ex.toBoolean
import com.github.zimoyin.qqbot.utils.ex.toJsonObject
import io.vertx.core.Future
import io.vertx.kotlin.core.json.jsonObjectOf


/**
 * 频道帖子列表
 * @param channel 频道
 * @param callback 回调
 * @author: zimo
 * @date:   2024/1/5 005
 */
@UntestedApi
fun HttpAPIClient.getPostList(
    channel: Channel,
    callback: ((List<ForumThread>) -> Unit)? = null,
): Future<List<ForumThread>> {
    val promise = promise<List<ForumThread>>()
    API.PostList
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.channelID!!)
        .send()
        .bodyJsonHandle(promise, "getPostList", "获取频道帖子列表失败") {
            if (!it.result) return@bodyJsonHandle
            val threadList = it.body.toJsonObject()
                .apply { if (!this.getInteger("is_finish").toBoolean()) logWarn("getPostList", "列表未能完全加载完毕") }
                .getJsonArray("threads")
                .map { it.toJsonObject() }
                .map { it.mapTo(ForumThread::class.java).apply { this.channel = channel } }
            promise.complete(threadList)
            callback?.let { it1 -> it1(threadList) }
        }
    return promise.future()
}


/**
 * 频道帖子详情
 *
 * @param channel 频道
 * @param threadID 帖子ID
 * @param callback 回调
 * @author: zimo
 * @date:   2024/1/5 005
 */
@UntestedApi
fun HttpAPIClient.getPostDetail(
    channel: Channel,
    threadID: String,
    callback: ((ForumThread) -> Unit)? = null,
): Future<ForumThread> {
    val promise = promise<ForumThread>()
    API.PostDetail
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.channelID!!, threadID)
        .send()
        .bodyJsonHandle(promise, "getPostDetail", "获取帖子详情失败") {
            if (!it.result) return@bodyJsonHandle
            val thread = it.body.toJsonObject().getJsonObject("thread").mapTo(ForumThread::class.java)
            promise.complete(thread)
            callback?.let { it1 -> it1(thread) }
        }
    return promise.future()
}


/**
 * 删除频道帖子
 *
 *
 * @param channel 频道
 * @param threadID 帖子ID
 * @param callback 回调
 * @author: zimo
 * @date:   2024/1/5 005
 */
@UntestedApi
fun HttpAPIClient.deletePost(
    channel: Channel,
    threadID: String,
    callback: ((Boolean) -> Unit)? = null,
): Future<Boolean> {
    val promise = promise<Boolean>()
    API.DeletePost
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.channelID!!, threadID)
        .send()
        .bodyJsonHandle(promise, "deletePost", "删除帖子失败") {
            if (it.result) {
                promise.complete(true)
                callback?.let { it1 -> it1(true) }
            } else {
                promise.complete(false)
                callback?.let { it1 -> it1(false) }
            }
        }
    return promise.future()
}


/**
 * 发布频道帖子
 *
 * @param channel 频道
 * @param title 标题
 * @param content 内容
 * @param format 帖子文本格式
 * @param callback 回调
 * @author: zimo
 * @date:   2024/1/5 005
 */
@UntestedApi
fun HttpAPIClient.publishPost(
    channel: Channel,
    title: String,
    content: String,
    format: PostFormat,
    callback: ((Boolean) -> Unit)? = null,
): Future<Boolean> {
    val promise = promise<Boolean>()
    API.PublishPost
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.channelID!!)
        .sendJsonObject(jsonObjectOf("title" to title, "content" to content, "format" to format.value))
        .bodyJsonHandle(promise, "publishPost", "发布帖子失败") {
            if (it.result) {
                promise.complete(true)
                callback?.let { it1 -> it1(true) }
            } else {
                promise.complete(false)
                callback?.let { it1 -> it1(false) }
            }
        }
    return promise.future()
}

/**
 * 帖子文本格式
 */
enum class PostFormat(val value: Int, val description: String) {
    FORMAT_TEXT(1, "普通文本"),
    FORMAT_HTML(2, "HTML"),
    FORMAT_MARKDOWN(3, "Markdown"),
    FORMAT_JSON(4, "JSON（content参数可参照RichText结构）");

    companion object {
        fun fromValue(value: Int): PostFormat? = entries.find { it.value == value }
    }
}
