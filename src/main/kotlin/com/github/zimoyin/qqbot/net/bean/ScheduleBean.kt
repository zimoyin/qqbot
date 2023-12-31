package com.github.zimoyin.qqbot.net.bean

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 日程提醒类型枚举
 */
enum class RemindType(val id: Int, val description: String) {
  NO_REMIND(0, "不提醒"),
  REMIND_AT_START(1, "开始时提醒"),
  REMIND_5_MINUTES_BEFORE(2, "开始前 5 分钟提醒"),
  REMIND_15_MINUTES_BEFORE(3, "开始前 15 分钟提醒"),
  REMIND_30_MINUTES_BEFORE(4, "开始前 30 分钟提醒"),
  REMIND_60_MINUTES_BEFORE(5, "开始前 60 分钟提醒");

  companion object {
    fun fromId(id: Int): RemindType? = values().firstOrNull { it.id == id }
  }
}

/**
 * 日程对象
 */
data class ScheduleBean(
  /**
   * 日程 ID
   */
  @field:JsonProperty("id")
  val id: String? = null,

  /**
   * 日程名称
   */
  @field:JsonProperty("name")
  val name: String? = null,

  /**
   * 日程描述
   */
  @field:JsonProperty("description")
  val description: String?= null,

  /**
   * 日程开始时间戳（毫秒）
   */
  @field:JsonProperty("start_timestamp")
  val startTimestamp: Long? = null,

  /**
   * 日程结束时间戳（毫秒）
   */
  @field:JsonProperty("end_timestamp")
  val endTimestamp: Long? = null,

  /**
   * 创建者
   */
  @field:JsonProperty("creator")
  val creator: MemberBean ?= null,

  /**
   * 日程开始时跳转到的子频道 ID
   */
  @field:JsonProperty("jump_channel_id")
  val jumpChannelID: String? = null,

  /**
   * 日程提醒类型
   */
  @field:JsonProperty("remind_type")
  val remindType0: String? = null,
){
  @JsonIgnore
  fun getRemindType(): RemindType? = RemindType.fromId(remindType0!!.toInt())
}
