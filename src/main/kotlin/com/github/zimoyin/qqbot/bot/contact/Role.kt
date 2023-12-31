package com.github.zimoyin.qqbot.bot.contact

import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.net.bean.ContactPermission
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.net.http.api.channel.*
import io.vertx.core.Future
import java.io.Serializable

class Role(
  /**
   * 身份组ID
   */
  override val id: String = "",

  /**
   * 名称
   */
  val name: String = "",

  /**
   * ARGB的HEX十六进制颜色值转换后的十进制数值
   */
  val color: Long = 0,

  /**
   * 是否在成员列表中单独展示: 0-否, 1-是
   */
  val hoist: Int = 0,

  /**
   * 人数
   */
  val number: Int = -1,

  /**
   * 成员上限
   */
  val memberLimit: Int = 30,

  val channel: Channel,
) : Contact, Serializable {
  override val botInfo: BotInfo = channel.botInfo

  @Throws(IllegalStateException::class)
  override fun send(message: MessageChain): Future<MessageChain> {
    throw IllegalStateException("Role can't send message")
  }

  /**
   * 获取频道身份组成员列表
   */
  fun getGuildRoleMembers(): Future<List<ChannelUser>> {
    return HttpAPIClient.getGuildRoleMembers(channel, id)
  }

  /**
   * 修改频道身份组
   * @param name 身份组名称
   * @param color 身份组颜色
   * @param hoist 是否在成员列表中显示
   *
   * 接口会修改传入的字段，不传入的默认不会修改，至少要传入一个参数。
   */
  @UntestedApi
  @JvmOverloads
  fun updateGuildRole(
    name: String? = null,
    color: Int? = null,
    hoist: Boolean? = null,
  ): Future<Role> {
    return HttpAPIClient.updateGuildRole(channel, id, name, color, hoist)
  }


  /**
   * 删除频道身份组
   * @param role 身份组
   */
  @UntestedApi
  fun deleteGuildRole(): Future<Boolean> {
    return HttpAPIClient.deleteGuildRole(channel, id)
  }

  /**
   * 创建频道身份组成员
   * @param user 成员
   */
  @UntestedApi
  fun addGuildRoleMember(user: User): Future<Role> {
    require(!channel.isChannel)
    return HttpAPIClient.addGuildRoleMember(channel, user.id, id)
  }

  /**
   * 删除频道身份组成员
   * @param user 成员
   */
  @UntestedApi
  fun deleteGuildRoleMember(user: User): Future<Role> {
    require(!channel.isChannel)
    return HttpAPIClient.deleteGuildRoleMember(channel, user.id, id)
  }


  /**
   * 获取子频道身份组权限
   */
  @OptIn(UntestedApi::class)
  fun getChannelRolePermissions(): Future<ContactPermission> {
    return HttpAPIClient.getChannelRolePermissions(channel, id)
  }

  /**
   * 修改子频道身份组权限
   */
  @OptIn(UntestedApi::class)
  fun updateChannelRolePermissions(permissions: ContactPermission): Future<Boolean> {
    return HttpAPIClient.updateChannelRolePermissions(channel, id, permissions)
  }

}
