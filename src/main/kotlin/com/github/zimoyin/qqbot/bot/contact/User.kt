package com.github.zimoyin.qqbot.bot.contact

import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.net.bean.ContactPermission
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.net.http.api.channel.*
import com.github.zimoyin.qqbot.net.bean.Message
import com.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future
import java.io.Serializable
import java.time.Instant

/**
 *
 * @author : zimo
 * @date : 2023/12/11
 */
interface User : Contact {
  /**
   * 用户id
   */
  override val id: String

  /**
   * 用户昵称
   */
  val nick: String

  /**
   * 是否是官方API开发的机器人
   */
  val isBot: Boolean

  /**
   * 用户头像地址
   */
  val avatar: String

  /**
   *  用户身份组id
   */
  val roles: List<String>

  /**
   *  用户加入频道的时间
   */
  val joinedAt: Instant

  /**
   * 特殊关联应用的 openid
   * 需要特殊申请并配置后才会返回
   */
  val unionOpenID: String?

  /**
   * 机器人关联的互联应用的用户信息
   * 与 union_openid 关联的应用是同一个
   */
  val unionUserAccount: String?

  /**
   * 禁言用户
   */
  fun mute(
    muteTimestamp: Long = 24 * 60 * 1000,
    muteEndTimestamp: Long = System.currentTimeMillis() + muteTimestamp,
  ): Future<Boolean> {
      val promise = promise<Boolean>()
      promise.fail(IllegalStateException("该用户无法在此聊天会话下被禁言"))
      return promise.future()
  }
}

open class Sender(
  override val id: String,
  override val nick: String,
  override val isBot: Boolean,
  override val avatar: String,
  override val roles: List<String>,
  override val joinedAt: Instant,
  override val unionOpenID: String?,
  override val unionUserAccount: String?,
  override val botInfo: BotInfo,
) : User, Serializable {
  companion object {
    fun convert(
      botInfo: BotInfo, message: Message,
    ): Sender = Sender(
      id = message.author!!.uid,
      nick = message.author.username!!,
      isBot = message.author.isBot ?: false,
      avatar = message.author.avatar ?: "none",
      roles = message.member?.roles ?: emptyList(),
      joinedAt = message.member?.joinedAt ?: Instant.now(),
      unionOpenID = message.author.unionOpenID,
      unionUserAccount = message.author.unionUserAccount,
      botInfo = botInfo
    )
  }

  override fun send(message: MessageChain): Future<MessageChain> {
    TODO("暂时无法向该用户发送私信")
  }
}


data class ChannelUser(
  override val id: String,
  override val nick: String,
  override val isBot: Boolean,
  override val avatar: String,
  override val roles: List<String>,
  override val joinedAt: Instant,
  override val unionOpenID: String?,
  override val unionUserAccount: String?,
  override val botInfo: BotInfo,
  val channel: Channel,
) : Sender(id, nick, isBot, avatar, roles, joinedAt, unionOpenID, unionUserAccount, botInfo) {
  companion object {
    fun convert(
      botInfo: BotInfo, message: Message,
    ): ChannelUser {
      return ChannelUser(
        id = message.author!!.uid,
        nick = message.author.username!!,
        isBot = message.author.isBot ?: false,
        avatar = message.author.avatar ?: "none",
        roles = message.member?.roles ?: emptyList(),
        joinedAt = message.member?.joinedAt ?: Instant.now(),
        unionOpenID = message.author.unionOpenID,
        unionUserAccount = message.author.unionUserAccount,
        botInfo = botInfo,
        channel = ChannelImpl.convert(botInfo, message),
      )
    }
  }

  override fun send(message: MessageChain): Future<MessageChain> {
    val promise = promise<MessageChain>()
    if (channel.currentID == channel.channelID) {
      promise.fail("当前机器人不允许向频道发送信息，只能向当前联系人发送信息。[频道无法获取到联系人临时ID]")
      return promise.future()
    }
    return HttpAPIClient.sendChannelPrivateMessageAsync(channel, message)
  }

  @JvmOverloads
  fun mute(muteTimestamp: Long = 24 * 60 * 1000): Future<Boolean> {
    return mute(muteTimestamp, System.currentTimeMillis() + muteTimestamp)
  }


  @OptIn(UntestedApi::class)
  override fun mute(muteTimestamp: Long, muteEndTimestamp: Long): Future<Boolean> {
    return HttpAPIClient.setChannelMuteMember(channel, id, muteTimestamp, muteEndTimestamp)
  }


  /**
   * 创建频道身份组成员
   * 用于将频道guild_id下的用户 user_id 添加到身份组 role_id
   *
   */
  @UntestedApi
  fun addGuildRoleMember(role: Role): Future<Role> {
    return HttpAPIClient.addGuildRoleMember(channel, id, role.id)
  }

  /**
   * 删除频道成员
   * @param userID 成员ID
   * @param addBlacklist 是否将用户加入黑名单
   * @param deleteHistoryMsg 是否删除用户的历史消息
   *
   */
  @UntestedApi
  @JvmOverloads
  fun deleteGuildMember(
    addBlacklist: Boolean = false,
    deleteHistoryMsg: MessageRevokeTimeRange = MessageRevokeTimeRange.NO_REVOKE,
  ): Future<Boolean> {
    return HttpAPIClient.deleteSubChannelMember(channel, id, addBlacklist, deleteHistoryMsg)
  }

  /**
   * 获取子频道用户权限
   */
  @OptIn(UntestedApi::class)
  fun getChannelPermissions(): Future<ContactPermission> {
    return HttpAPIClient.getChannelPermissions(channel, id)
  }

  /**
   * 修改子频道用户权限
   */
  @OptIn(UntestedApi::class)
  fun updateChannelPermissions(permissions: ContactPermission): Future<Boolean> {
    return HttpAPIClient.updateChannelPermissions(channel,id,permissions)
  }
}
