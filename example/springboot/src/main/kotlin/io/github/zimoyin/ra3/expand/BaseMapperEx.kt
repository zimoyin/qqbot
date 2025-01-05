package io.github.zimoyin.ra3.expand

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper

/**
 *
 * @author : zimo
 * @date : 2025/01/05
 */
inline fun <reified T : Any> BaseMapper<T>.selectCount(callback: KtQueryWrapper<T>.() -> Unit): Long {
    return this.selectCount(KtWrappers.lambdaQuery<T>().apply(callback))
}

inline fun <reified T : Any> BaseMapper<T>.selectIsExist(callback: KtQueryWrapper<T>.() -> Unit): Boolean {
    return this.selectCount(KtWrappers.lambdaQuery<T>().apply(callback)) > 0
}