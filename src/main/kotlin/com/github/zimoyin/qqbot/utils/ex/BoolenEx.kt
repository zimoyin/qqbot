package com.github.zimoyin.qqbot.utils.ex

/**
 *
 * @author : zimo
 * @date : 2023/12/28
 */
fun Boolean.toInt(): Int {
    return if (this) 1 else 0
}