package com.github.zimoyin.qqbot.net

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.zimoyin.qqbot.net.http.toHeaders
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
    val appSecret: String = "",
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
    var version: Int = -1
):Serializable {

    init {
        if (version == -1){
            version = if (token.isNotEmpty()) 1
            else if (appSecret.isNotEmpty()) 2
            else throw IllegalArgumentException("Token authentication method not specified")
        }
    }

    fun getHeaders(): HeadersMultiMap  {
        return when (version){
            1 -> getHeadersV1()
            2 -> getHeadersV2()
            else -> throw IllegalArgumentException("Unsupported version: $version")
        }
    }
    /**
     * 老版本鉴权
     */
    private fun getHeadersV1(): HeadersMultiMap = hashMapOf(
        "Authorization" to "Bot $appID.$token",
    ).toHeaders()

    /**
     * 使用最新的鉴权方式，注意需要频繁更新 accessToken
     */
    private fun getHeadersV2(): HeadersMultiMap = hashMapOf(
        "Authorization" to "QQBot $accessToken",
        "X-Union-Appid" to "QQBot $appID"
    ).toHeaders()

}

