package io.github.zimoyin.qqbot.net.http.api

import io.github.zimoyin.qqbot.LocalLogger
import io.github.zimoyin.qqbot.exception.HttpClientException
import io.github.zimoyin.qqbot.exception.HttpHandlerException
import io.github.zimoyin.qqbot.exception.HttpStateCodeException
import io.github.zimoyin.qqbot.utils.ex.isInitialStage
import io.github.zimoyin.qqbot.utils.ex.promise
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
    val logger = LocalLogger(HttpAPIClient::class.java)


    init {
        logger.info("API Client 日志框架准备完成，如果需要关闭该日志请手段排除该路径")
    }

    fun logError(apiName: String, msg: String, it: Throwable) {
        logger.error("API Client [$apiName]: $msg", HttpClientException(it))
    }

    fun logError(apiName: String, msg: String, it: String) {
        logger.error("API Client [$apiName]: $msg", HttpClientException(it))
    }

    fun logError(apiName: String, msg: String) {
        logger.error("API Client [$apiName]: $msg")
    }

    /**
     * 如果当前promise 中处于初始状态，则输出错误日志
     */
    fun <T> logPreError(promise: Promise<T>, apiName: String, msg: String): Boolean {
        if (promise.isInitialStage()) {
            logError(apiName, msg)
            return true
        }
        return false
    }

    /**
     * 如果当前promise 中处于初始状态，则输出错误日志
     */
    fun <T> logPreError(promise: Promise<T>, apiName: String, msg: String, e: Throwable): Boolean {
        if (promise.isInitialStage()) {
            logError(apiName, msg, e)
            return true
        }
        return false
    }

    fun apiError(apiName: String, result: JsonObject) {
        logger.error("API Client [$apiName]: API 使用方式错误 -> [${result.getInteger("code")}] ${result.getString("message")}")
    }

    fun apiError(apiName: String, code: Int, msg: String) {
        logger.error("API Client [$apiName]: API 使用方式错误 -> [$code] $msg ")
    }

    fun logDebug(apiName: String, msg: String) {
        if (API.isDebug) logger.debug("API Client [$apiName]: $msg")
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

    /**
     * 处理响应
     * @param errorMessage 上层指定的错误信息
     */
    fun <T> Future<HttpResponse<Buffer>>.bodyJsonHandle(
        promise: Promise<T>,
        apiName: String,
        errorMessage: String,
        callback: (val0: APIJsonResult) -> Unit,
    ): Future<HttpResponse<Buffer>> {
        return this.onSuccess {
            logDebug(apiName, "API Response Code: ${it.statusCode()}")
            onSuccess0(promise, apiName, errorMessage, it, callback)
        }.onFailure {
            if (!promise.tryFail(it) || promise.isInitialStage()) logError(apiName, errorMessage, it)
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

            // 尝试解析json
            kotlin.runCatching {
                json = response.bodyAsJsonObject()
                code = json?.getInteger("code")
                message = json?.getString("message")
                if (code != null && message != null) {
                    accessFailed = true
                }
            }

            // 解析失败
            if (accessFailed) {
                kotlin.runCatching {
                    //处理API访问失败的情况，callback 需要使用 promise 告知处理结果，否则会在下个流程抛出异常
                    callback(APIJsonResult(json, false, response, errorMessage = message ?: "未知错误: API 访问失败"))
                }.onFailure {
                    //callback 执行失败，保险机制
                    logPreError(promise, apiName, callbackErrorMessage, it)
                    if (!promise.tryFail(HttpClientException(it))) {
                        logError(apiName, callbackErrorMessage, it)
                    }
                }
                //避免 callback 不promise 告知处理结果，导致流程卡住
                if (!promise.tryFail(HttpHandlerException("API 错误 -> [$code] $message"))) {
                    logDebug(apiName, "API 错误 -> [$code] $message")
                }

                return@runCatching
            }

            // 访问失败，状态码错误
            if (status < 200 || status >= 400) {
                kotlin.runCatching {
                    //处理API访问失败的情况，callback 需要使用 promise 告知处理结果，否则会在下个流程抛出异常
                    callback(APIJsonResult(json, false, response, errorMessage = message ?: "未知错误: API 访问失败"))
                }.onFailure {
                    //保险机制
                    logPreError(promise, apiName, callbackErrorMessage, it).let { isLog ->
                        if (!promise.tryFail(HttpStateCodeException(it))) {
                            if (!isLog) logError(apiName, callbackErrorMessage, it)
                        }
                    }
                }

                //避免 callback 不promise 告知处理结果，导致流程卡住
                // 如果 JSON 返回了 code  和 message
                // 否则 返回 Http Code
                logPreError(promise, apiName, "API 错误(HttpCode: $status) -> [$code] $message").let { isLog ->
                    if ((code != null || message != null) && !promise.tryFail(HttpClientException("API 错误 -> [$code] $message"))) {
                        if (!isLog) logError(apiName, "API 错误 -> [$code] $message")
                    } else if (!promise.tryFail(HttpStateCodeException("API 错误 -> Http Code : $status"))) {
                        if (!isLog) logError(apiName, "API 错误 -> Http Code : $status")
                    }
                }
                return@runCatching
            }

            // 处理API访问成功的情况
            kotlin.runCatching {
                //处理API访问成功的情况，callback 需要使用 promise 告知处理结果，否则会在下个流程抛出异常
                callback(APIJsonResult(json, true, response))
                return
            }.onFailure {
                logPreError(promise, apiName, "API 错误 -> [$code] $message").let { isLog ->
                    promise.tryFail(
                        HttpHandlerException(
                            "API access successful, an internal exception occurred during API result callback call", it
                        )
                    ).apply {
                        if (!this && !isLog) logError(apiName, errorMessage, it)
                    }
                }
            }
            //保险机制
            logPreError(
                promise, apiName, "API 访问成功，由于 callback 未能使用 promise 告知处理结果，导致流程卡住"
            ).let { isLog ->
                promise.tryFail(HttpClientException("严重错误，API访问成功，由于 callback 未能使用 promise 告知处理结果，导致流程卡住"))
                    .apply {
                        if (!this && !isLog) logError(
                            apiName, "API 访问成功，由于 callback 未能使用 promise 告知处理结果，导致流程卡住"
                        )
                    }
            }

        }.onFailure {
            kotlin.runCatching {
                callback(APIJsonResult(null, false, response))
            }.onFailure { e ->
                e.addSuppressed(it)
                logPreError(promise, apiName, callbackErrorMessage, it).let { isLog ->
                    if (!promise.tryFail(HttpClientException(e))) {
                        if (!isLog) logError(apiName, errorMessage, e)
                    }
                }
            }

            logPreError(promise, apiName, "API 访问失败 -> ${it.message}").let { isLog ->
                if (!promise.tryFail(HttpClientException(it))) {
                    if (!isLog) logError(apiName, errorMessage, it)
                }
            }

        }
    }


    data class APIJsonResult(
        val json: JsonObject?,
        val result: Boolean,
        val httpResponse: HttpResponse<Buffer>,
        val body: Buffer = httpResponse.body() ?: Buffer.buffer(),
        val errorMessage: String? = null,
    )


}
