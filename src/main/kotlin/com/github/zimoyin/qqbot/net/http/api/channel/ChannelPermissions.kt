package com.github.zimoyin.qqbot.net.http.api.channel

import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.net.http.addRestfulParam
import com.github.zimoyin.qqbot.net.http.api.API
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future


/**
 * 修改子频道
 *
 * @param channel 频道
 * @param name 名称
 * @param position 排序
 * @param parentID 父频道ID
 * @param privateType 私密类型
 * @param speakPermission 说话权限
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/28 028
 */
@UntestedApi
fun HttpAPIClient.getChannelPermissions(
  channel: Channel,
  userID: String,
  name: String? = null,
  callback: ((Channel) -> Unit)? = null,
): Future<Channel> {
  val promise = promise<Channel>()
  API.UpdateSubChannel.addRestfulParam(channel.channelID!!,userID).send().onSuccess {

  }.onFailure {
    logError("UpdateSubChannel", "修改子频道失败", it)
    promise.fail(it)
  }
  return promise.future()
}
