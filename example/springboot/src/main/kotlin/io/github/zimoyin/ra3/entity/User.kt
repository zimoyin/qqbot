package io.github.zimoyin.ra3.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

@TableName("users")
data class User(
    val id: Long? = null,
    val name: String? = null,
    val age: Int? = null,
)