package io.github.zimoyin.qqbot.net

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.zimoyin.qqbot.net.http.toHeaders
import io.vertx.core.http.impl.headers.HeadersMultiMap
import java.io.Serializable

/**
 *
 * @author : zimo
 * @date : 2023/12/06/23:51
 * 适用于一二代鉴权的Token
 */
data class Token(
    @field:JsonProperty("appId")
    val appID: String,
    @field:JsonIgnore
    val token: String = "",
    @field:JsonProperty("clientSecret")
    val clientSecret: String = "",
    /**
     * 请求 AccessToken 以获取和更新
     */
    @field:JsonIgnore
    var accessToken: String = "",
    @field:JsonIgnore
    var expiresIn: Int = -1,

    /**
     * 用户选择的鉴权版本
     */
    @field:JsonIgnore
    var version: Int = -1,
) : Serializable {

    init {
        if (version == -1) {
            version = if (clientSecret.isNotEmpty()) 2
            else if (token.isNotEmpty()) 1
            else throw IllegalArgumentException("Token authentication method not specified. Please set the appid, token or clientSecret")
        }
    }

    @JsonIgnore
    fun getHeaders(version0: Int = version): HeadersMultiMap {
        return when (version0) {
            1 -> getHeadersV1()
            2 -> getHeadersV2()
            else -> throw IllegalArgumentException("Unsupported version: $version0")
        }
    }

    @JvmName("version")
    fun putVersion(version: Int): Token {
        this.version = version
        return this
    }

    /**
     * 老版本鉴权
     */
    @JsonIgnore
    private fun getHeadersV1(): HeadersMultiMap = hashMapOf(
        "Authorization" to getAuthorization(1),
    ).toHeaders()

    /**
     * 使用最新的鉴权方式，注意需要频繁更新 accessToken
     */
    @JsonIgnore
    private fun getHeadersV2(): HeadersMultiMap = hashMapOf(
        "Authorization" to getAuthorization(2),
        "X-Union-Appid" to "QQBot $appID"
    ).toHeaders()

    fun getTokenVerify(version0: Int = version): String {
        return when (version0) {
            1 -> "$appID.$token"
            2 -> accessToken
            else -> throw IllegalArgumentException("Unsupported version: $version")
        }
    }

    fun getAuthorization(version0: Int = version): String {
        return when (version0) {
            1 -> "Bot ${getTokenVerify(1)}"
            2 -> "QQBot ${getTokenVerify(2)}"
            else -> throw IllegalArgumentException("Unsupported version: $version")
        }
    }


    companion object {
        @JvmStatic
        @JvmOverloads
        fun create(appID: String, token: String, appSecret: String = ""): Token {
            return Token(appID, token, appSecret)
        }

        @JvmStatic
        fun createByToken(appID: String, token: String): Token {
            return Token(appID, token)
        }

        @JvmStatic
        fun createByAppSecret(appID: String, appSecret: String): Token {
            return Token(appID, "", appSecret)
        }
    }
}

