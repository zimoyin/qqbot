package io.github.zimoyin.qqbot.net.http.api.channel

import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.net.bean.AnnouncesBean
import io.github.zimoyin.qqbot.net.bean.RecommendChannelBean
import io.github.zimoyin.qqbot.net.http.addRestfulParam
import io.github.zimoyin.qqbot.net.http.api.API
import io.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.github.zimoyin.qqbot.utils.ex.mapTo
import io.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future


/**
 * 创建频道公告
 * 公告类型分为 消息类型的频道公告 和 推荐子频道类型的频道公告
 * @param channel 频道
 * @param messageID 消息ID 信息ID请从频道消息中获取，使用他能创建一个 消息类型的频道公告
 * @param welcomeAnnouncement 是否是欢迎公告
 * @param recommend 推荐子频道
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/31 031
 */
@UntestedApi
fun HttpAPIClient.createChannelAnnouncement(
    channel: Channel,
    messageID: String? = null,
    welcomeAnnouncement: Boolean = false,
    recommend: List<RecommendChannelBean> = arrayListOf(),
    callback: ((AnnouncesBean) -> Unit)? = null,
): Future<AnnouncesBean> {
    val promise = promise<AnnouncesBean>()
    val bean = AnnouncesBean(channel.guildID, channel.id, messageID, if (welcomeAnnouncement) 1 else 0, recommend)
    val json0 = bean.toJson()
    API.CreateChannelAnnouncement
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.guildID)
        .sendJsonObject(json0)
        .bodyJsonHandle(promise, "createChannelAnnouncement", "创建公告失败") {
            if (!it.result) return@bodyJsonHandle
            val list = it.body.mapTo(AnnouncesBean::class.java)
            promise.complete(list)
            callback?.let { it1 -> it1(list) }
        }
    return promise.future()
}


/**
 * 删除频道公告
 * @param channel 频道
 * @param messageID 消息ID
 *
 * @author: zimo
 * @date:   2023/12/31 031
 */
@UntestedApi
fun HttpAPIClient.deleteChannelAnnouncement(
    channel: Channel,
    messageID: String,
    callback: ((Boolean) -> Unit)? = null,
): Future<Boolean> {
    val promise = promise<Boolean>()
    API.CreateChannelAnnouncement
        .putHeaders(channel.botInfo.token.getHeaders())
        .addRestfulParam(channel.guildID, messageID)
        .send()
        .bodyJsonHandle(promise, "deleteChannelAnnouncement", "删除公告失败") {
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

