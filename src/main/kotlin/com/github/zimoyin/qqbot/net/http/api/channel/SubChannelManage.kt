package com.github.zimoyin.qqbot.net.http.api.channel

import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.bot.contact.ChannelImpl
import com.github.zimoyin.qqbot.net.http.addRestfulParam
import com.github.zimoyin.qqbot.net.http.api.API
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.net.http.api.channel.MessageRevokeTimeRange.ALL_MESSAGES
import com.github.zimoyin.qqbot.net.http.api.channel.MessageRevokeTimeRange.NO_REVOKE
import com.github.zimoyin.qqbot.net.bean.ChannelBean
import com.github.zimoyin.qqbot.utils.ex.mapTo
import com.github.zimoyin.qqbot.utils.ex.promise
import com.github.zimoyin.qqbot.utils.ex.toJsonObject
import com.github.zimoyin.qqbot.utils.ex.writeToText
import io.vertx.core.Future
import io.vertx.core.json.JsonObject


/**
 * create 频道
 *
 * @param channel 频道
 * @param subChannel 子频道
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/28 028
 */
@UntestedApi
fun HttpAPIClient.creatSubChannel(
  channel: Channel,
  subChannel: ChannelBean,
  callback: ((Channel) -> Unit)? = null,
): Future<Channel> {
  val promise = promise<Channel>()
  API.CreateSubChannel.putHeaders(channel.botInfo.token.getHeaders()).addRestfulParam(channel.guildID)
      .sendJsonObject(subChannel.toJsonObject())
      .bodyJsonHandle(promise,"CreateSubChannel","创建子频道失败"){
          if (!it.result) return@bodyJsonHandle
          val bean = it.body.toJsonObject().getJsonObject("role").mapTo(ChannelBean::class.java)
          val mapToChannel = ChannelImpl.convert(channel.botInfo, bean)
          promise.complete(mapToChannel)
          callback?.let { it1 -> it1(mapToChannel) }
      }
  return promise.future()
}


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
fun HttpAPIClient.updateSubChannel(
  channel: Channel,
  name: String? = null,
  position: Int? = null,
  parentID: String? = null,
  privateType: Int? = null,
  speakPermission: Int? = null,
  callback: ((Channel) -> Unit)? = null,
): Future<Channel> {
  val promise = promise<Channel>()
  val param = JsonObject().apply {
    name?.let { put("name", it) }
    position?.let { put("position", it) }
    parentID?.let { put("parent_id", it) }
    privateType?.let { put("private_type", it) }
    speakPermission?.let { put("speak_permission", it) }
  }
  API.UpdateSubChannel.putHeaders(channel.botInfo.token.getHeaders()).addRestfulParam(channel.channelID!!).sendJsonObject(param)
      .bodyJsonHandle(promise,"UpdateSubChannel","修改子频道失败"){
          if (!it.result) return@bodyJsonHandle
          val bean = it.body.mapTo(ChannelBean::class.java)
          val mapToChannel = ChannelImpl.convert(channel.botInfo, bean)
          promise.complete(mapToChannel)
          callback?.let { it1 -> it1(mapToChannel) }
      }
  return promise.future()
}


/**
 * 删除子频道
 *
 * @param channel 频道
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/28 028
 */
@UntestedApi
fun HttpAPIClient.deleteSubChannel(
  channel: Channel,
  callback: ((Boolean) -> Unit)? = null,
): Future<Boolean> {
  val promise = promise<Boolean>()
  API.DeleteSubChannel.putHeaders(channel.botInfo.token.getHeaders()).addRestfulParam(channel.channelID!!).send()
      .bodyJsonHandle(promise,"DeleteSubChannel","删除子频道失败"){
          if (it.result){
              promise.complete(true)
              callback?.let { it1 -> it1(true) }
          }else{
              promise.complete(false)
              callback?.let { it1 -> it1(false) }
          }
      }
  return promise.future()
}


/**
 * 删除子频道成员
 *
 * @param channel 频道
 * @param callback 回调
 *
 * @author: zimo
 * @date:   2023/12/28 028
 */
@UntestedApi
fun HttpAPIClient.deleteSubChannelMember(
  channel: Channel,
  userID: String,
  addBlacklist: Boolean = false,
  deleteHistoryMsg: MessageRevokeTimeRange = MessageRevokeTimeRange.NO_REVOKE,
  callback: ((Boolean)
  -> Unit)? = null,
): Future<Boolean> {
  val promise = promise<Boolean>()
  val param = JsonObject().apply {
    put("add_blacklist", addBlacklist)
    put("delete_history_msg_days", deleteHistoryMsg.value)
  }
  API.DeleteSubChannelMember.putHeaders(channel.botInfo.token.getHeaders()).addRestfulParam(channel.channelID!!, userID)
      .sendJsonObject(param)
      .bodyJsonHandle(promise,"DeleteSubChannelMember","删除子频道成员失败"){
          if (it.result){
              promise.complete(true)
              callback?.let { it1 -> it1(true) }
          }else{
              promise.complete(false)
              callback?.let { it1 -> it1(false) }
          }
      }
  return promise.future()
}

/**
 * 描述消息撤回时间范围的枚举。
 * 提供了固定的天数范围：3天、7天、15天和30天。
 * 特殊的时间范围包括：
 * - [ALL_MESSAGES]：撤回全部消息。
 * - [NO_REVOKE]（默认值）：不撤回任何消息。
 */
enum class MessageRevokeTimeRange(val value: Int) {
  /**
   * 消息撤回时间范围：3天。
   */
  THREE_DAYS(3),

  /**
   * 消息撤回时间范围：7天。
   */
  SEVEN_DAYS(7),

  /**
   * 消息撤回时间范围：15天。
   */
  FIFTEEN_DAYS(15),

  /**
   * 消息撤回时间范围：30天。
   */
  THIRTY_DAYS(30),

  /**
   * 特殊时间范围：撤回全部消息。
   */
  ALL_MESSAGES(-1),

  /**
   * 默认值：不撤回任何消息。
   */
  NO_REVOKE(0)
}

