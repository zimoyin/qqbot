package io.github.zimoyin.qqbot.utils.ex

import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

/**
 *
 * @author : zimo
 * @date : 2024/12/19
 */
fun ByteArray.toBase64(): String {
    return Base64.getEncoder().encodeToString(this)
}

fun ByteArray.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(this)
    val noSign = BigInteger(1, digest).toString(16)
    return noSign.padStart(32, '0')
}
