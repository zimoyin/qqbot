package io.github.zimoyin.ra3.entity

import com.baomidou.mybatisplus.annotation.*
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.github.zimoyin.ra3.config.LocalDateTimeDeserializer
import io.github.zimoyin.ra3.config.LocalDateTimeSerializer
import java.time.LocalDateTime

/**
 * CREATE TABLE IF NOT EXISTS users
 * (
 *     id          INTEGER PRIMARY KEY AUTOINCREMENT,
 *     username    VARCHAR(255) NOT NULL,
 *     uid         VARCHAR(255) NOT NULL,
 *     create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
 * );
 */
@TableName("users")
class User(
    val username: String,
    val uid: String,
    val cid: Int,
    @get:JsonSerialize(using = LocalDateTimeSerializer::class)
    val createTime: LocalDateTime = LocalDateTime.now(),
    @TableId(type = IdType.AUTO)
    val id: Long? = null,
){
}
