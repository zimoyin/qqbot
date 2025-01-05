package io.github.zimoyin.ra3.expand

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.core.toolkit.Wrappers
import com.baomidou.mybatisplus.core.toolkit.Wrappers.emptyWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import io.github.zimoyin.ra3.entity.User

/**
 *
 * @author : zimo
 * @date : 2025/01/05
 */

class KtWrappers private constructor() {
    companion object {
        inline fun <reified T : Any> lambdaQueryKt(): KtQueryWrapper<T> {
            return KtQueryWrapper<T>(T::class.java)
        }

        fun <T : Any> lambdaQueryKt(clzz: Class<T>): KtQueryWrapper<T> {
            return KtQueryWrapper<T>(clzz)
        }

        fun <T : Any> lambdaQueryKt(obj: T): KtQueryWrapper<T> {
            return KtQueryWrapper<T>(obj)
        }

        inline fun <reified T : Any> lambdaUpdateKt(): KtUpdateWrapper<T> {
            return KtUpdateWrapper<T>(T::class.java)
        }

        fun <T : Any> lambdaUpdateKt(clzz: Class<T>): KtUpdateWrapper<T> {
            return KtUpdateWrapper<T>(clzz)
        }

        fun <T : Any> lambdaUpdateKt(obj: T): KtUpdateWrapper<T> {
            return KtUpdateWrapper<T>(obj)
        }

        fun <T> query(): QueryWrapper<T> {
            return Wrappers.query()
        }

        fun <T> query(t: T): QueryWrapper<T> {
            return Wrappers.query(t)
        }

        fun <T> query(clzz: Class<T>): QueryWrapper<T> {
            return Wrappers.query(clzz)
        }

        fun <T> update(): UpdateWrapper<T> {
            return Wrappers.update()
        }

        fun <T> update(t: T): UpdateWrapper<T> {
            return Wrappers.update(t)
        }

        fun <T> emptyWrapper(): QueryWrapper<T> {
            return Wrappers.emptyWrapper()
        }
    }
}

fun <T> QueryWrapper<T>.toKt(): KtQueryWrapper<*> {
    return KtQueryWrapper(this)
}

fun <T> UpdateWrapper<T>.toKt(): KtUpdateWrapper<*> {
    return KtUpdateWrapper(this)
}