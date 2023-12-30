package com.github.zimoyin.qqbot.utils.ex

import com.fasterxml.jackson.databind.JsonNode
import com.github.zimoyin.qqbot.utils.Color
import com.github.zimoyin.qqbot.utils.JSON
import java.net.URL

fun String.toUrl(): URL {
    try {
        return URL(this)
    } catch (e: Exception) {
        throw IllegalArgumentException("url is invalid", e)
    }
}

fun String.toJAny(): JsonNode {
    return JSON.toJAny(this)
}

inline fun <reified T : Any> String.mapTo(): T {
    return JSON.toObject<T>(this)
}

/**
 * 将十六进制颜色转换颜色类
 * @author: zimo
 * @date:   2023/12/27 027
 */
fun String.toColor(hexString: String): Color {
    require(hexString.startsWith("#")) { "Invalid HEX color format. It should start with '#'." }
    require(hexString.length == 9) { "Invalid HEX color format. It should be in the format #AARRGGBB." }

    val hexValue = hexString.substring(1).toInt(16)
    return Color(hexValue)
}