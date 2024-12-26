package io.github.zimoyin.qqbot.net.http.api

import io.github.zimoyin.qqbot.exception.HttpClientException
import io.github.zimoyin.qqbot.net.Token
import io.github.zimoyin.qqbot.net.bean.BotUser
import io.github.zimoyin.qqbot.utils.JSON
import io.github.zimoyin.qqbot.utils.ex.mapTo
import io.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future
import io.vertx.core.json.JsonObject


/**
 *
 * @author : zimo
 * @date : 2023/12/21
 * 更新动态令牌，异步
 */
@JvmOverloads
fun HttpAPIClient.accessToken(token: Token, isUpdate: Boolean = true): Future<String> {
    val promise = promise<String>()
    API.AccessToken.sendJsonObject(JSON.toJsonObject(token)).onSuccess {
        runCatching {
            val json = kotlin.runCatching { it.bodyAsJsonObject() }.getOrNull()
            if (json == null) {
                throw HttpClientException("[AccessToken] Token 更新失败: 响应不是JSON对象: ${it.bodyAsString()}")
            }
            if (json.getInteger("code") != null) {
                throw HttpClientException("[AccessToken] Token 更新失败: ${json.getString("message")}")
            }
            if (isUpdate) token.accessToken = json.getString("access_token")
            if (isUpdate) token.expiresIn = json.getString("expires_in").toInt()
            json.encode()
        }.onFailure { e ->
            logPreError(promise, "accessToken", "获取 Token 失败: ${e.message}", e).let {
                promise.tryFail(HttpClientException("Get Token failed", e))
            }
        }.onSuccess {
            promise.tryComplete(it)
        }
    }.onFailure { e ->
        logPreError(promise, "accessToken", "获取 Token 失败: ${e.message}", e).let {
            promise.tryFail(HttpClientException("Get Token failed", e))
        }
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
            val user = it.body().mapTo(BotUser::class.java)
            promise.tryComplete(user)
        }.onFailure { e ->
            logPreError(promise, "botInfo", "获取机器人信息失败: ${it.bodyAsString()}").let {b->
                promise.tryFail(HttpClientException("Get BotInfo failed: ${it.bodyAsString()}", e))
            }
        }
    }.onFailure { e ->
        logPreError(promise, "botInfo", "获取机器人信息失败: ${e.message}", e).let {
            promise.tryFail(HttpClientException("Get BotInfo failed", e))
        }
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
            logPreError(promise, "gateway", "解析 WSS 接入点失败: ${resp.bodyAsString()}", e).let {
                promise.tryFail(HttpClientException("Get Gateway failed", e))
            }
        }
    }.onFailure { e ->
        logPreError(promise, "gateway", "解析 WSS 接入点失败", e).let {
            promise.tryFail(HttpClientException("Get Gateway failed", e))
        }
    }
    return promise.future()
}
