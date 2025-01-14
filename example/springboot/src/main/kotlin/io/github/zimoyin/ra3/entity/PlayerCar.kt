package io.github.zimoyin.ra3.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.github.zimoyin.ra3.config.LocalDateTimeSerializer
import java.sql.Timestamp
import java.time.LocalDateTime

@TableName("player_car")
class PlayerCar(
    val uid: String,
    var level: Long = 0,
    var egressTimes: Long = -1,
    @get:JsonSerialize(using = LocalDateTimeSerializer::class)
    var updateTime: LocalDateTime = LocalDateTime.now(),
    @get:JsonSerialize(using = LocalDateTimeSerializer::class)
    val createTime: LocalDateTime? = null,
    @TableId(type = IdType.AUTO)
    val tid: Long?= null,
)
