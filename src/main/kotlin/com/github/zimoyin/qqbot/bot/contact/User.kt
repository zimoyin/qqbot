package com.github.zimoyin.qqbot.bot.contact

import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.net.websocket.bean.Message
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
  fun mute(muteTimestamp: Long = 24 * 60 * 1000, muteEndTimestamp: Long = System.currentTimeMillis() + muteTimestamp) {
    TODO("该用户无法在此创建下被禁言")
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
    //TODO 等待实现
    TODO("暂时无法向该用户发送私信")
  }
}
