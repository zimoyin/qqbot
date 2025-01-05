package io.github.zimoyin.ra3.entity

import com.baomidou.mybatisplus.annotation.*
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
    val createTime: LocalDateTime = LocalDateTime.now(),
    @TableId(type = IdType.AUTO)
    val id: Int? = null,
){
}
