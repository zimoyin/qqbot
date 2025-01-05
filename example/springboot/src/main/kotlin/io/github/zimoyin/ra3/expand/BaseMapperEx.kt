package io.github.zimoyin.ra3.expand

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.core.toolkit.Wrappers
import io.github.zimoyin.ra3.entity.User
import io.github.zimoyin.ra3.mapper.UsersMapper

/**
 *
 * @author : zimo
 * @date : 2025/01/05
 */
fun <T> BaseMapper<T>.selectCount(callback: LambdaQueryWrapper<T>.() -> Unit): Long {
    return this.selectCount(Wrappers.lambdaQuery<T>().apply(callback))
}