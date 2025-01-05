package io.github.zimoyin.ra3.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.github.zimoyin.ra3.config.LocalDateTimeDeserializer
import io.github.zimoyin.ra3.config.LocalDateTimeSerializer
import java.sql.Timestamp
import java.time.LocalDateTime

@TableName("player_car")
class PlayerCar(
    val uid: String,
    val level: Long = 0,
    val egressTimes: Long = -1,
    @get:JsonSerialize(using = LocalDateTimeSerializer::class)
    val createTime: LocalDateTime = LocalDateTime.now(),
    @get:JsonSerialize(using = LocalDateTimeSerializer::class)
    val updateTime: LocalDateTime = LocalDateTime.now(),
    @TableId(type = IdType.AUTO)
    val tid: Long?= null,
)
