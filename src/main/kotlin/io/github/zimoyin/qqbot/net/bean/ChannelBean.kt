package io.github.zimoyin.qqbot.net.bean

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 子频道对象数据类 (Channel)
 * 注意：子频道对象中所涉及的 ID 类数据，都仅在机器人场景流通，与真实的 ID 无关。
 */
data class ChannelBean(
  /**
   * 子频道 ID
   */
  @field:JsonProperty("id")
  val id: String = "",

  /**
   * 频道 ID
   */
  @field:JsonProperty("guild_id")
  val guildID: String = "",

  /**
   * 子频道名
   */
  @field:JsonProperty("name")
  val name: String? = null,

  /**
   * 子频道类型 [ChannelType]
   */
  @field:JsonProperty("type")
  val type: Int? = null,

  /**
   * 子频道子类型 [ChannelSubType]
   */
  @field:JsonProperty("sub_type")
  val subType: Int? = null,

  /**
   * 排序值，具体请参考 有关 position 的说明
   *
   * postiton 从 1 开始
   * 当子频道类型为 子频道分组（ChannelType=4）时，由于 position 1 被未分组占用，所以 position 只能从 2 开始
   * 如果不传默认追加到分组下最后一个
   * 如果填写一个已经存在的值，那么会插入在原来的元素之前
   * 如果填写一个较大值，与不填是相同的表现，同时存储的值会根据真实的 position 进行重新计算，并不会直接使用传入的值
   */
  @field:JsonProperty("position")
  val position: Int? = null,

  /**
   * 所属分组 ID，仅对子频道有效，对 子频道分组（ChannelType=4） 无效
   */
  @field:JsonProperty("parent_id")
  val parentId: String? = null,

  /**
   * 创建人 ID
   */
  @field:JsonProperty("owner_id")
  val ownerId: String = "0",

  /**
   * 子频道私密类型 [PrivateType]
   */
  @field:JsonProperty("private_type")
  val privateType: Int? = null,

  /**
   * 子频道发言权限 [SpeakPermission]
   */
  @field:JsonProperty("speak_permission")
  val speakPermission: Int? = null,

  /**
   * 用于标识应用子频道应用类型，仅应用子频道时会使用该字段，具体定义请参考 应用子频道的应用类型
   */
  @field:JsonProperty("application_id")
  val applicationId: String? = null,

  /**
   * 用户拥有的子频道权限 [Permissions]
   */
  @field:JsonProperty("permissions")
  val permissions: String? = null,

  /**
   * 子频道操作者 ID,只在子频道事件时才会填充
   */
  @field:JsonProperty("op_user_id")
  val opUserId: String? = null,

  @field:JsonProperty("private_user_ids")
  val privateUserIDs: List<String> = arrayListOf(),
) : Serializable {

  val channelID: String
    @JsonIgnore get() = id

  @JsonIgnore
  fun getChannelType(): ChannelType? {
    return ChannelType.fromValue(type ?: -1)
  }

  @JsonIgnore
  fun getChannelSubType(): ChannelSubType? {
    return if (type == ChannelType.TEXT_CHANNEL.value) {
      ChannelSubType.fromValue(subType ?: -1)
    } else {
      null
    }
  }

  @JsonIgnore
  fun getPrivateType(): PrivateType? {
    return PrivateType.fromValue(privateType ?: -1)
  }

  @JsonIgnore
  fun getSpeakPermission(): SpeakPermission? {
    return SpeakPermission.fromValue(speakPermission ?: -1)
  }

  companion object {
    /**
     * 创建子频道 Bean
     * @param name 子频道名
     * @param type 子频道类型
     * @param subType 子频道子类型
     * @param position 排序值，具体请参考 有关 position 的说明
     * @param parentId 所属分组 ID，仅对子频道有效，对 子频道分组（ChannelType=4） 无效
     * @param privateType 子频道私密类型
     * @param speakPermission 子频道发言权限
     * @param applicationId 用于标识应用子频道应用类型，仅应用子频道时会使用该字段，具体定义请参考 应用子频道的应用类型
     */
    @JvmOverloads
    fun create(
      name: String,
      type: ChannelType = ChannelType.TEXT_CHANNEL,
      subType: ChannelSubType = ChannelSubType.CHAT,
      position: Int = 3,
      parentId: String = "0",
      privateType: PrivateType = PrivateType.PUBLIC,
      speakPermission: SpeakPermission = SpeakPermission.EVERYONE,
      applicationId: String? = null,
    ) = ChannelBean(
      name = name,
      type = type.value,
      subType = subType.value,
      position = position,
      parentId = parentId,
      privateType = privateType.value,
      speakPermission = speakPermission.value,
      applicationId = applicationId,
    )
  }
}

// 注意：以下枚举类型需要自行定义
enum class ChannelType(val value: Int, val description: String) : Serializable {
  TEXT_CHANNEL(0, "文字子频道"),
  RESERVED_1(1, "保留，不可用"),
  VOICE_CHANNEL(2, "语音子频道"),
  RESERVED_3(3, "保留，不可用"),
  CHANNEL_GROUP(4, "子频道分组"),
  LIVE_CHANNEL(10005, "直播子频道"),
  APP_CHANNEL(10006, "应用子频道"),
  FORUM_CHANNEL(10007, "论坛子频道");

  companion object {
    fun fromValue(value: Int): ChannelType? {
      return entries.firstOrNull { it.value == value }
    }
  }

}

enum class ChannelSubType(val value: Int, val description: String) : Serializable {
  CHAT(0, "闲聊"),
  NOTICE(1, "公告"),
  GUIDE(2, "攻略"),
  TEAM_UP(3, "开黑");

  companion object {
    fun fromValue(value: Int): ChannelSubType? {
      return entries.firstOrNull { it.value == value }
    }
  }

}

enum class PrivateType(val value: Int, val description: String) : Serializable {
  PUBLIC(0, "公开频道"),
  OWNER_ADMIN_VISIBLE(1, "群主管理员可见"),
  DESIGNATED_MEMBERS(2, "群主管理员+指定成员，可使用 修改子频道权限接口 指定成员");

  companion object {
    fun fromValue(value: Int): PrivateType? {
      return entries.firstOrNull { it.value == value }
    }
  }

}

enum class SpeakPermission(val value: Int, val description: String) : Serializable {
  INVALID_TYPE(0, "无效类型"),
  EVERYONE(1, "所有人"),
  OWNER_ADMIN_DESIGNATED_MEMBERS(2, "群主管理员+指定成员，可使用 修改子频道权限接口 指定成员");

  companion object {
    fun fromValue(value: Int): SpeakPermission? {
      return entries.firstOrNull { it.value == value }
    }
  }

}
