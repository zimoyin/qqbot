package com.github.zimoyin.qqbot.net.http.api

import com.github.zimoyin.qqbot.exception.HttpClientException
import com.github.zimoyin.qqbot.net.Token
import com.github.zimoyin.qqbot.net.bean.BotUser
import com.github.zimoyin.qqbot.utils.JSON
import com.github.zimoyin.qqbot.utils.ex.mapTo
import com.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future
import io.vertx.core.json.JsonObject


/**
 *
 * @author : zimo
 * @date : 2023/12/21
 * 更新动态令牌，异步
 */
fun HttpAPIClient.accessTokenUpdateAsync(token: Token): Future<String> {
    val promise = promise<String>()
    API.AccessToken.sendJsonObject(JSON.toJsonObject(token)).onSuccess {
        runCatching {
            val json = it.bodyAsJsonObject()
            if (json.getInteger("code") != null) {
                throw HttpClientException("[AccessToken] Token 更新失败: ${json.getString("message")}")
            }
            token.accessToken = json.getString("access_token")
            token.expiresIn = json.getString("expires_in").toInt()
//            logDebug("AccessToken", "Token 更新成功")
            json.encode()
        }.onFailure {
            promise.tryFail(it)
        }.onSuccess {
            promise.tryComplete(it)
        }
    }.onFailure {
        promise.tryFail(it)
    }
    return promise.future()
}

/**
 * 机器人信息获取
 */
fun HttpAPIClient.botInfo(token: Token): Future<BotUser> {
    val promise = promise<BotUser>()
    API.BotInfo.putHeaders(token.getHeaders()).send().onSuccess {
        runCatching {
            promise.tryComplete(it.body().mapTo(BotUser::class.java))
        }.onFailure {
            promise.tryFail(it)
        }
    }.onFailure {
        promise.tryFail(it)
    }
    return promise.future()
}


/**
 * 获取 WSS 接入点
 */
fun HttpAPIClient.gatewayToWssUrl(token: Token): Future<String> {
    val promise = promise<String>()
    API.Gateway.putHeaders(token.getHeaders()).send().onSuccess { resp ->
        runCatching {
            promise.tryComplete(resp.bodyAsJsonObject().getString("url"))
        }
    }.onFailure {
        logError("gateway", "无法获取到 WSS 接入点", it)
        promise.tryFail(it)
    }
    return promise.future()
}

/**
 * 获取 WSS 接入点 V2
 */
fun HttpAPIClient.gatewayV2Async(token: Token): Future<JsonObject> {
    val promise = promise<JsonObject>()
    API.GatewayV2.putHeaders(token.getHeaders()).send().onSuccess { resp ->
        try {
            promise.tryComplete(resp.bodyAsJsonObject())
        } catch (e: Exception) {
            logError("gateway", "解析 WSS 接入点失败: ${resp.bodyAsString()}", e)
            promise.tryFail(e)
        }
    }.onFailure {
        logError("gateway", "无法获取到 WSS 接入点", it)
    }
    return promise.future()
}
