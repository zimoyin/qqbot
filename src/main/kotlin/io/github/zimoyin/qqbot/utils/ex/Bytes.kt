package io.github.zimoyin.qqbot.utils.ex

import java.util.*

/**
 *
 * @author : zimo
 * @date : 2024/12/19
 */
fun ByteArray.toBase64(): String {
    return Base64.getEncoder().encodeToString(this)
}
