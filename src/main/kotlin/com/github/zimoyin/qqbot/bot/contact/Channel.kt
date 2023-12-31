package com.github.zimoyin.qqbot.bot.contact

import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.net.bean.*
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.net.http.api.channel.*
import com.github.zimoyin.qqbot.utils.Color
import io.vertx.core.Future

/**
 *
 * @author : zimo
 * @date : 2023/12/09
 *
 * 以后 频道 ID 不作为 联系人最先直观获取的ID 而是称为父亲ID，如果没有就是他本身
 */
interface Channel : Contact {
  /**
   * 频道ID 与 channelID 一致
   */
  override val id: String

  /**
   * 频道ID
   */
  val guildID: String

  /**
   * 子频道ID
   */
  val channelID: String?

  /**
   * 如果是私信就是 srcGuildID(临时ID) 否则就是 channelID （为空就是 guildID）
   */
  val currentID: String

  /**
   * 是否是频道
   */
  val isGuild: Boolean
    get() = channelID == null || channelID != guildID

  /**
   * 是否是子频道
   */
  val isChannel: Boolean
    get() = channelID != null && channelID != guildID

  /**
   * 获取频道详情
   */
  fun getGuildDetails(): Future<GuildBean> {
    return HttpAPIClient.getGuildDetails(channel = this)
  }

  /**
   * 获取子频道详情
   */
  fun getChannelDetails(): Future<ChannelBean> {
    require(isChannel) { "This channel not is sub channel" }
    return HttpAPIClient.getChannelDetails(this)
  }

  /**
   * 获取子频道列表
   */
  fun getChannels(): Future<List<Channel>> {
    return HttpAPIClient.getChannels(this)
  }

  /**
   * 获取子频道列表信息
   * 该方法区别于 getChannels() 该方法只返回子频道信息
   */
  fun getChannelInfos(): Future<List<ChannelBean>> {
    return HttpAPIClient.getChannelInfos(this)
  }

  /**
   * 获取频道成员列表
   *
   */
  fun getGuildMembers(): Future<List<ChannelUser>> {
    return HttpAPIClient.getGuildMembers(this)
  }

  /**
   * 获取子频道在线成员数
   * 注意: 用于查询音视频/直播子频道 channel_id 的在线成员数
   */
  fun getLiveChannelOnlineTotal(): Future<Int> {
    require(isChannel) { "This channel not is sub channel" }
    return HttpAPIClient.getChannelOnlineMemberSize(this)
  }

  /**
   * 子频道管理
   */
  val channelOperate: ChannelOperate
    get() = ChannelOperate(this)

  /**
   * 频道资料/内容管理
   */
  val assetManagement: AssetManagement
    get() = AssetManagement(this)

  /**
   * 频道身份组管理
   */
  val guildRoles: GuildRoles
    get() = GuildRoles(this)

  /**
   * 频道权限管理
   */
  val authorization: Authorization
    get() = Authorization(this)

  /**
   * 频道禁言管理
   */
  val mute: Mute
    get() = Mute(this)

  //TODO 小程序相关
}

data class ChannelImpl(
  override val id: String,
  override val guildID: String,
  override val channelID: String?,
  override val currentID: String,
  override val botInfo: BotInfo,
) : Channel {
  companion object {
    fun convert(info: BotInfo, message: Message): ChannelImpl = ChannelImpl(
      id = message.guildID!!,
      guildID = message.guildID,
      channelID = message.channelID,
      currentID = message.srcGuildID ?: message.channelID ?: message.guildID,
      botInfo = info
    )

    fun convert(info: BotInfo, guildID: String, channelID: String?, srcGuildID: String?): ChannelImpl = ChannelImpl(
      id = guildID,
      guildID = guildID,
      channelID = channelID,
      currentID = srcGuildID ?: channelID ?: guildID,
      botInfo = info
    )

    fun convert(info: BotInfo, channelBean: ChannelBean): ChannelImpl = ChannelImpl(
      id = channelBean.guildID,
      guildID = channelBean.guildID,
      channelID = channelBean.channelID,
      currentID = channelBean.channelID,
      botInfo = info
    )
  }

  override fun send(message: MessageChain): Future<MessageChain> {
    return if (currentID == channelID) HttpAPIClient.sendChannelMessageAsync(this, message)
    else HttpAPIClient.sendChannelPrivateMessageAsync(this, message)
  }
}

/**
 * 身份组管理类，提供获取、创建、更新、删除身份组以及管理身份组成员等功能。
 */
class GuildRoles(val channel: Channel) {
  /**
   * 获取频道身份组列表
   */
  fun getGuildRoles(): Future<GuildRolesBean> {
    return HttpAPIClient.getGuildRoles(channel)
  }

  /**
   * 获取频道身份组成员列表
   */
  fun getGuildRoleMembers(roleID: String): Future<List<ChannelUser>> {
    return HttpAPIClient.getGuildRoleMembers(channel, roleID)
  }

  /**
   * 创建频道身份组
   * @param name 身份组名称
   * @param color 身份组颜色
   * @param hoist 是否在成员列表中显示
   */
  @UntestedApi
  @JvmOverloads
  fun createGuildRole(
    name: String = "0",
    color: Int = Color.TRANSPARENT_BLACK.toArgb(),
    hoist: Boolean = true,
  ): Future<Role> {
    return HttpAPIClient.createGuildRole(channel, name, color, hoist)
  }

  /**
   * 修改频道身份组
   * @param roleID 身份组ID
   * @param name 身份组名称
   * @param color 身份组颜色
   * @param hoist 是否在成员列表中显示
   *
   * 接口会修改传入的字段，不传入的默认不会修改，至少要传入一个参数。
   */
  @UntestedApi
  @JvmOverloads
  fun updateGuildRole(
    roleID: String,
    name: String? = null,
    color: Int? = null,
    hoist: Boolean? = null,
  ): Future<Role> {
    return HttpAPIClient.updateGuildRole(channel, roleID, name, color, hoist)
  }


  /**
   * 删除频道身份组
   * @param roleID 身份组ID
   */
  @UntestedApi
  fun deleteGuildRole(roleID: String): Future<Boolean> {
    return HttpAPIClient.deleteGuildRole(channel, roleID)
  }

  /**
   * 创建频道身份组成员
   * 用于将频道guild_id下的用户 user_id 添加到身份组 role_id
   */
  @UntestedApi
  fun addGuildRoleMember(user: User, roleID: String): Future<Role> {
    require(!channel.isChannel)
    return addGuildRoleMember(user.id, roleID)
  }

  /**
   * 创建频道身份组成员
   * 用于将频道guild_id下的用户 user_id 添加到身份组 role_id
   */
  @UntestedApi
  fun addGuildRoleMember(userID: String, roleID: String): Future<Role> {
    require(!channel.isChannel)
    return HttpAPIClient.addGuildRoleMember(channel, userID, roleID)
  }

  /**
   * 删除频道身份组成员
   */
  @UntestedApi
  fun deleteGuildRoleMember(userID: User, roleID: String): Future<Role> {
    require(!channel.isChannel)
    return deleteGuildRoleMember(userID.id, roleID)
  }

  /**
   * 删除频道身份组成员
   */
  @UntestedApi
  fun deleteGuildRoleMember(userID: String, roleID: String): Future<Role> {
    require(!channel.isChannel)
    return HttpAPIClient.deleteGuildRoleMember(channel, userID, roleID)
  }
}

/**
 * 子频道管理类，提供创建、更新、删除子频道以及删除频道成员等功能。
 *
 */
class ChannelOperate(val channel: Channel) {

  /**
   * 创建子频道
   * @param subChannel 子频道信息
   * 请使用 ChannelBean.crate(...) 进行创建
   *
   * @return 子频道信息
   */
  @UntestedApi
  fun createChannel(subChannel: ChannelBean): Future<Channel> {
    return HttpAPIClient.creatSubChannel(channel, subChannel)
  }

  /**
   * 更新子频道
   * 需要修改哪个字段，就传递哪个字段即可。
   * @param name 子频道名称
   * @param position 子频道排序位置
   * @param parentId 父频道ID
   * @param privateType 子频道私密性
   * @param speakPermission 子频道发言权限
   */
  @UntestedApi
  @JvmOverloads
  fun updateChannel(
    name: String? = null,
    position: Int? = null,
    parentId: String? = null,
    privateType: PrivateType? = null,
    speakPermission: SpeakPermission? = null,
  ): Future<Channel> {
    require(!channel.isChannel)
    return HttpAPIClient.updateSubChannel(
      channel, name, position, parentId, privateType?.value, speakPermission?.value
    )
  }

  /**
   * 删除子频道
   */
  @OptIn(UntestedApi::class)
  fun deleteChannel(): Future<Boolean> {
    require(!channel.isChannel)
    return HttpAPIClient.deleteSubChannel(channel)
  }


  /**
   * 删除频道成员
   * @param userID 成员ID
   * @param addBlacklist 是否将用户加入黑名单
   * @param deleteHistoryMsg 是否删除用户的历史消息
   */
  @UntestedApi
  @JvmOverloads
  fun deleteGuildMember(
    userID: String,
    addBlacklist: Boolean = false,
    deleteHistoryMsg: MessageRevokeTimeRange = MessageRevokeTimeRange.NO_REVOKE,
  ): Future<Boolean> {
    return HttpAPIClient.deleteSubChannelMember(channel, userID, addBlacklist, deleteHistoryMsg)
  }

  /**
   * 删除频道成员
   * @param userID 成员ID
   * @param addBlacklist 是否将用户加入黑名单
   * @param deleteHistoryMsg 是否删除用户的历史消息
   */
  @UntestedApi
  @JvmOverloads
  fun deleteGuildMember(
    userID: User,
    addBlacklist: Boolean = false,
    deleteHistoryMsg: MessageRevokeTimeRange = MessageRevokeTimeRange.NO_REVOKE,
  ): Future<Boolean> {
    return HttpAPIClient.deleteSubChannelMember(channel, userID.id, addBlacklist, deleteHistoryMsg)
  }
}

/**
 * 频道内容管理类，提供对频道公告、精华消息、日程、音频控制、机器人上/下麦、帖子等频道内容的管理功能。
 */
class AssetManagement(val channel: Channel) {
  fun createChannelAnnouncement() {
    //TODO 创建频道公告
  }

  fun deleteChannelAnnouncement() {
    //TODO 删除频道公告
  }

  fun addEssentialMessage() {
    //TODO 添加精华消息
  }

  fun deleteEssentialMessage() {
    //TODO 删除精华消息
  }

  fun getEssentialMessages() {
    //TODO 获取精华消息
  }

  fun getChannelScheduleList() {
    //TODO 获取频道日程列表
  }

  fun getScheduleDetails() {
    //TODO 获取日程详情
  }

  fun createSchedule() {
    //TODO 创建日程
  }

  fun modifySchedule() {
    //TODO 修改日程
  }

  fun deleteSchedule() {
    //TODO 删除日程
  }

  fun audioControl() {
    //TODO 音频控制
  }

  fun robotOnStage() {
    //TODO 机器人上麦
  }

  fun robotOffStage() {
    //TODO 机器人下麦
  }

  fun getPostList() {
    //TODO 获取帖子列表
  }

  fun getPostDetails() {
    //TODO 获取帖子详情
  }

  fun publishPost() {
    //TODO 发表帖子
  }

  fun deletePost() {
    //TODO 删除帖子
  }
}

/**
 * 权限管理类，提供对子频道用户权限、身份组权限以及机器人在频道接口权限的获取、修改和授权链接发送等功能。
 */
class Authorization(val channel: Channel) {
  /**
   * 获取子频道用户权限
   */
  @OptIn(UntestedApi::class)
  fun getChannelPermissions(user: User): Future<ContactPermission> {
    return HttpAPIClient.getChannelPermissions(channel, user.id)
  }

  /**
   * 获取子频道用户权限
   */
  @OptIn(UntestedApi::class)
  fun getChannelPermissions(userID: String): Future<ContactPermission> {
    return HttpAPIClient.getChannelPermissions(channel, userID)
  }

  /**
   * 修改子频道用户权限
   */
  @OptIn(UntestedApi::class)
  fun updateChannelPermissions(user: User, permissions: ContactPermission): Future<Boolean> {
    return HttpAPIClient.updateChannelPermissions(channel, user.id, permissions)
  }

  /**
   * 修改子频道用户权限
   */
  @OptIn(UntestedApi::class)
  fun updateChannelPermissions(userID: String, permissions: ContactPermission): Future<Boolean> {
    return HttpAPIClient.updateChannelPermissions(channel, userID, permissions)
  }

  /**
   * 获取子频道身份组权限
   */
  @OptIn(UntestedApi::class)
  fun getChannelRolePermissions(role: Role): Future<ContactPermission> {
    return HttpAPIClient.getChannelRolePermissions(channel, role.id)
  }

  /**
   * 获取子频道身份组权限
   */
  @OptIn(UntestedApi::class)
  fun getChannelRolePermissions(roleID: String): Future<ContactPermission> {
    return HttpAPIClient.getChannelRolePermissions(channel, roleID)
  }

  /**
   * 修改子频道身份组权限
   */
  @OptIn(UntestedApi::class)
  fun updateChannelRolePermissions(role: Role, permissions: ContactPermission): Future<Boolean> {
    return HttpAPIClient.updateChannelRolePermissions(channel, role.id, permissions)
  }

  /**
   * 修改子频道身份组权限
   */
  @OptIn(UntestedApi::class)
  fun updateChannelRolePermissions(roleID: String, permissions: ContactPermission): Future<Boolean> {
    return HttpAPIClient.updateChannelRolePermissions(channel, roleID, permissions)
  }

  /**
   * 获取机器人在频道可用权限列表
   */
  fun getBotPermissions(): Future<List<APIPermission>> {
    return HttpAPIClient.getChannelBotPermissions(channel)
  }

  /**
   * 发送机器人在频道接口权限的授权链接
   */
  @OptIn(UntestedApi::class)
  fun sendAuthLink(permissions: APIPermission): Future<APIPermissionDemand> {
    return HttpAPIClient.demandChannelBotPermissions(channel, permissions)
  }
}

/**
 * 禁言
 */
class Mute(val channel: Channel) {
  /**
   * 获取频道消息频率的设置详情
   */
  @UntestedApi
  fun getChannelRate(): Future<MessageSetting> {
    return HttpAPIClient.getChannelRate(channel)
  }

  /**
   * 频道全员禁言，包括子频道
   */
  @UntestedApi
  @JvmOverloads
  fun setChannelMute(
    muteTimestamp: Long = 24 * 60 * 1000,
    muteEndTimestamp: Long = System.currentTimeMillis() + muteTimestamp,
  ): Future<Boolean> {
    return HttpAPIClient.setChannelMute(channel, muteTimestamp, muteEndTimestamp)
  }

  /**
   * 频道指定成员禁言
   * 该接口同样支持解除指定成员禁言，将mute_end_timestamp或mute_seconds传值为字符串'0'即可
   */
  @UntestedApi
  @JvmOverloads
  fun setChannelMuteMember(
    userID: String,
    muteTimestamp: Long = 24 * 60 * 1000,
    muteEndTimestamp: Long = System.currentTimeMillis() + muteTimestamp,
  ): Future<Boolean> {
    return HttpAPIClient.setChannelMuteMember(channel, userID, muteTimestamp, muteEndTimestamp)
  }

  /**
   * 频道指定成员禁言
   * 该接口同样支持解除指定成员禁言，将mute_end_timestamp或mute_seconds传值为字符串'0'即可
   */
  @UntestedApi
  @JvmOverloads
  fun setChannelMuteMember(
    user: User,
    muteTimestamp: Long = 24 * 60 * 1000,
    muteEndTimestamp: Long = System.currentTimeMillis() + muteTimestamp,
  ): Future<Boolean> {
    return setChannelMuteMember(user.id, muteTimestamp, muteEndTimestamp)
  }


  /**
   * 频道批量成员禁言
   */
  @UntestedApi
  @JvmOverloads
  fun setChannelMuteMembers(
    vararg users: MemberBean,
    muteTimestamp: Long = 24 * 60 * 1000,
    muteEndTimestamp: Long = System.currentTimeMillis() + muteTimestamp,
  ): Future<Boolean> {
    return HttpAPIClient.setChannelMuteMembers(channel, users.map { it.user!!.uid }, muteTimestamp, muteEndTimestamp)
  }

  /**
   * 频道批量成员禁言
   */
  @UntestedApi
  @JvmOverloads
  fun setChannelMuteMembers(
    vararg userID: String,
    muteTimestamp: Long = 24 * 60 * 1000,
    muteEndTimestamp: Long = System.currentTimeMillis() + muteTimestamp,
  ): Future<Boolean> {
    return HttpAPIClient.setChannelMuteMembers(channel, userID.toList(), muteTimestamp, muteEndTimestamp)
  }
}

