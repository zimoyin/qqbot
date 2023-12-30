package com.github.zimoyin.qqbot.net.http.api

import com.github.zimoyin.qqbot.exception.HttpClientException
import io.vertx.core.Future
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

    fun Future<HttpResponse<Buffer>>.logError(apiName: String, msg: String): Future<HttpResponse<Buffer>> {
        val thr = HttpClientException()
        this.onFailure {
            thr.initCause(it)
            logError(apiName, msg, it)
        }
        return this
    }
}