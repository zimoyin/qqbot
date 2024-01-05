package com.github.zimoyin.qqbot.net.http.api

import com.github.zimoyin.qqbot.exception.HttpClientException
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.HttpResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * API 访问入口
 */
object HttpAPIClient {

    val logger: Logger by lazy { LoggerFactory.getLogger(API::class.java) }
    fun logError(apiName: String, msg: String, it: Throwable) {
        logger.error("API Client [$apiName]: $msg", HttpClientException(it))
    }

    fun logError(apiName: String, msg: String, it: String) {
        logger.error("API Client [$apiName]: $msg", HttpClientException(it))
    }

    fun logError(apiName: String, msg: String) {
        logger.error("API Client [$apiName]: $msg")
    }

    fun apiError(apiName: String, result: JsonObject) {
        logger.error("API Client [$apiName]: API 使用方式错误 -> [${result.getInteger("code")}] ${result.getString("message")}")
    }

    fun apiError(apiName: String, code: Int, msg: String) {
        logger.error("API Client [$apiName]: API 使用方式错误 -> [$code] $msg ")
    }

    fun logDebug(apiName: String, msg: String) {
        logger.debug("API Client [$apiName]: $msg")
    }

    fun logWarn(apiName: String, msg: String) {
        logger.warn("API Client [$apiName]: $msg")
    }

    fun Future<HttpResponse<Buffer>>.logError(apiName: String, msg: String): Future<HttpResponse<Buffer>> {
        val thr = HttpClientException()
        this.onFailure {
            thr.initCause(it)
            logError(apiName, msg, it)
        }
        return this
    }

    fun <T> Future<HttpResponse<Buffer>>.bodyJsonHandle(
        promise: Promise<T>,
        apiName: String,
        errorMessage: String,
        callback: (val0: APIJsonResult) -> Unit,
    ): Future<HttpResponse<Buffer>> {
        return this.onSuccess {
            logDebug(apiName, "API Response Code: ${it.statusCode()}")
            onSuccess0<T>(promise, apiName, errorMessage, it, callback)
        }.onFailure {
            logError(apiName, errorMessage, it)
        }
    }

    private fun <T> onSuccess0(
        promise: Promise<T>,
        apiName: String,
        errorMessage: String,
        response: HttpResponse<Buffer>,
        callback: (r: APIJsonResult) -> Unit,
    ) {
        val callbackErrorMessage = "处理回调失败"
        kotlin.runCatching {
            val status = response.statusCode()
            var json: JsonObject? = null
            var code: Int? = -1
            var message: String? = null
            var accessFailed: Boolean = false
            kotlin.runCatching {
                json = response.bodyAsJsonObject()
                code = json?.getInteger("code")
                message = json?.getString("message")
                if (code != null && message != null) {
                    accessFailed = true
                }
            }
            if (accessFailed) {
                logError(
                    "apiName", "result -> [$code] $message"
                )
                kotlin.runCatching {
                    //处理API访问失败的情况，callback 需要使用 promise 告知处理结果，否则会在下个流程抛出异常
                    callback(APIJsonResult(json, false, response))
                }.onFailure {
                    logError(apiName, callbackErrorMessage, it)
                    promise.tryFail(it) //callback 执行失败，保险机制
                }
                //避免 callback 不promise 告知处理结果，导致流程卡住
                promise.tryFail("API 错误 -> [$code] $message")
                return@runCatching
            }

            if (status < 200 || status >= 300) {
//            if (status != 200 && status != 204) {
                if (code != null) logError(
                    "apiName", "result -> [$code] $message"
                ) else logError(
                    "apiName", "API 访问失败"
                )
                kotlin.runCatching {
                    //处理API访问失败的情况，callback 需要使用 promise 告知处理结果，否则会在下个流程抛出异常
                    callback(APIJsonResult(json, false, response))
                }.onFailure {
                    logError(apiName, callbackErrorMessage, it)
                    promise.tryFail(it) //保险机制
                }
                //避免 callback 不promise 告知处理结果，导致流程卡住
                if (code != null || message != null) promise.tryFail("API 错误 -> [$code] $message")
                promise.tryFail("API 错误 -> Http Code : $status")
                return@runCatching
            }

            kotlin.runCatching {
                //处理API访问成功的情况，callback 需要使用 promise 告知处理结果，否则会在下个流程抛出异常
                callback(APIJsonResult(json, true, response))
            }.onFailure {
                logError(apiName, errorMessage, it)
                promise.tryFail(it) //保险机制
            }
            //保险机制
            promise.tryFail("严重错误，API访问成功，由于 callback 未能使用 promise 告知处理结果，导致流程卡住")
        }.onFailure {
            kotlin.runCatching {
                callback(APIJsonResult(null, false, response))
            }.onFailure {
                logError(apiName, callbackErrorMessage, it)
            }
            logError(apiName, errorMessage, it)
            promise.tryFail(it)
        }
    }


    data class APIJsonResult(
        val json: JsonObject?,
        val result: Boolean,
        val httpResponse: HttpResponse<Buffer>,
        val body: Buffer = httpResponse.body() ?: Buffer.buffer(),
    )


}
