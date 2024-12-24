package io.github.zimoyin.qqbot.net.bean

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.zimoyin.qqbot.utils.ex.toJsonObject
import io.vertx.core.json.JsonObject
import java.io.Serializable

/**
 * 定义公告对象（Announces）数据类，提供默认值 null
 */
data class AnnouncesBean(
  /**
   * 频道ID
   */
  @field:JsonProperty("guild_id")
  val guildID: String? = null,

  /**
   * 子频道ID
   */
  @field:JsonProperty("channel_id")
  val channelID: String? = null,

  /**
   * 公告消息ID
   */
  @field:JsonProperty("message_id")
  val messageID: String? = null,

  /**
   * 公告类别，0代表成员公告，1代表欢迎公告，默认为成员公告
   */
  @field:JsonProperty("announces_type")
  val announcesType: Int = 1,

  /**
   * 推荐子频道详情列表
   */
  @field:JsonProperty("recommend_channels")
  val recommendChannels: List<RecommendChannelBean> = arrayListOf(),
) : Serializable {
  @JsonIgnore
  fun toJson(): JsonObject {
    val json = this.toJsonObject()
    if (recommendChannels.isEmpty()) json.remove("recommend_channels")
    channelID ?: json.remove("channel_id")
    messageID ?: json.remove("message_id")
    return json
  }
}

/**
 * 定义推荐子频道对象（RecommendChannel）数据类，提供默认值 null
 */
data class RecommendChannelBean(
  /**
   * 子频道ID
   */
  @field:JsonProperty("channel_id")
  val channelID: String? = null,

  /**
   * 推荐语
   */
  @field:JsonProperty("introduce")
  val introduce: String = "",
) : Serializable
