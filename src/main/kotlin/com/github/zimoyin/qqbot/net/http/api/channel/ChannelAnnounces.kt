package com.github.zimoyin.qqbot.net.http.api.channel

import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.net.bean.AnnouncesBean
import com.github.zimoyin.qqbot.net.bean.RecommendChannelBean
import com.github.zimoyin.qqbot.net.http.addRestfulParam
import com.github.zimoyin.qqbot.net.http.api.API
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.utils.ex.promise
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
    .onSuccess {
      kotlin.runCatching {
        val json = it.bodyAsJsonObject()
        val code = json.getInteger("code")
        val message = json.getString("message")
        if (code == null && message == null) {
          val list = json.mapTo(AnnouncesBean::class.java)
          promise.complete(list)
          callback?.let { it1 -> it1(list) }
        } else {
          logError(
            "createChannelAnnouncement", "result -> [$code] $message"
          )
        }
      }.onFailure {
        logError("createChannelAnnouncement", "发布公告失败", it)
        promise.fail(it)
      }
    }.onFailure {
      logError("createChannelAnnouncement", "发布公告失败", it)
      promise.fail(it)
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
    .onSuccess {
      kotlin.runCatching {
        if (it.statusCode() != 204) {
          val json = it.bodyAsJsonObject()
          val code = json.getInteger("code")
          val message = json.getString("message")
          if (code != null || message != null) {
            logError(
              "deleteChannelAnnouncement", "result -> [$code] $message"
            )
          }
          promise.complete(false)
          callback?.let { it1 -> it1(false) }
        } else {
          promise.complete(true)
          callback?.let { it1 -> it1(true) }
        }

      }.onFailure {
        logError("deleteChannelAnnouncement", "删除公告失败", it)
        promise.fail(it)
      }
    }.onFailure {
      logError("deleteChannelAnnouncement", "删除公告失败", it)
      promise.fail(it)
    }
  return promise.future()
}

