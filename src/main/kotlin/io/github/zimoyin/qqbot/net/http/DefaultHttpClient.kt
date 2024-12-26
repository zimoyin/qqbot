package io.github.zimoyin.qqbot.net.http

import io.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import io.github.zimoyin.qqbot.LocalLogger
import io.github.zimoyin.qqbot.utils.ex.awaitToCompleteExceptionally
import io.github.zimoyin.qqbot.utils.ex.toUrl
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.impl.headers.HeadersMultiMap
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.HttpRequest
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import java.net.URI
import java.net.URL


/**
 * 创建一个 通用的 HttpClient
 */
class DefaultHttpClient(
    val vertx: Vertx = GLOBAL_VERTX_INSTANCE,
    val options: WebClientOptions = WebClientOptions()
        .setConnectTimeout(5000)
        .setKeepAlive(true)
        .setSsl(true)
        .setTrustAll(true)
        .setFollowRedirects(true)
        .setMaxRedirects(10)
        .setDefaultPort(443)
        .setPoolCleanerPeriod(5000)
        .setPoolEventLoopSize(32)
        .setMaxPoolSize(64)
        .setMaxWaitQueueSize(-1),
    /**
     * 是否使用使用 head 查看服务器是否支持 SSL。如果设置为 false 则不自动修改 http 协议.(只对get/post/等方法有效)
     * 注意：不针对 HEAD 方法
     */
    var isHeadSSL: Boolean = true
) {
    val DefaultPorts: Map<String, Int> = HashMap<String, Int>().apply {
        put("http", 80)
        put("https", 443)
        put("ftp", 21)
        put("ssh", 22)
        put("smtp", 25)
        put("pop3", 110)
    }

    private val logger = LocalLogger(DefaultHttpClient::class.java)

    val DefaultClient: WebClient by lazy {
        WebClient.create(vertx, options)
    }

    /**
     * 创建一个专用于与某服务器通信的HttpClient
     * 服务器地址通过 options 设置
     */
    fun createClient(options: WebClientOptions, vertx: Vertx = GLOBAL_VERTX_INSTANCE): WebClient {
        return WebClient.create(vertx, options)
    }

    /**
     * 创建一个 GET 请求
     * 并在创建前检测是否支持 SSL(来自于 isHeadSSL 字段设置)
     */
    fun createGet(uri: URI): HttpRequest<Buffer> = DefaultClient.get(uri.toString()).apply {
        runCatching { ssl(uri.toURL().isSSl()) }
    }

    /**
     * 创建一个 GET 请求
     * 并在创建前检测是否支持 SSL(来自于 isHeadSSL 字段设置)
     */
    fun createGet(url: String): HttpRequest<Buffer> {
        val toUrl = url.toUrl()
        return DefaultClient.get(toUrl.cPort(), toUrl.host, url).ssl(toUrl.isSSl())
    }

    /**
     * 创建一个 POST 请求
     * 并在创建前检测是否支持 SSL(来自于 isHeadSSL 字段设置)
     */
    fun createPost(uri: URI): HttpRequest<Buffer> = DefaultClient.post(uri.toString()).apply {
        runCatching { ssl(uri.toURL().isSSl()) }
    }

    /**
     * 创建一个 POST 请求
     * 并在创建前检测是否支持 SSL(来自于 isHeadSSL 字段设置)
     */
    fun createPost(url: String): HttpRequest<Buffer> {
        val toUrl = url.toUrl()
        return DefaultClient.post(toUrl.cPort(), toUrl.host, url).ssl(toUrl.isSSl()).followRedirects(true)
    }

    /**
     * 创建一个请求
     * 并在创建前检测是否支持 SSL(来自于 isHeadSSL 字段设置)
     */
    fun createRequest(method: HttpMethod, uri: URI): HttpRequest<Buffer> =
        DefaultClient.request(method, uri.toString()).apply {
            runCatching { ssl(uri.toURL().isSSl()) }
        }

    /**
     * 创建一个请求
     * 并在创建前检测是否支持 SSL(来自于 isHeadSSL 字段设置)
     */
    fun createRequest(method: HttpMethod, url: String): HttpRequest<Buffer> {
        return DefaultClient.request(method, url.toUrl().cPort(), url.toUrl().host, url)
            .ssl(url.toUrl().isSSl())
    }

    /**
     * 创建一个 DELETE 请求
     * 并在创建前检测是否支持 SSL(来自于 isHeadSSL 字段设置)
     */
    fun createDelete(uri: URI): HttpRequest<Buffer> = DefaultClient.delete(uri.toString()).apply {
        runCatching { ssl(uri.toURL().isSSl()) }
    }

    /**
     * 创建一个 DELETE 请求
     * 并在创建前检测是否支持 SSL(来自于 isHeadSSL 字段设置)
     */
    fun createDelete(url: String): HttpRequest<Buffer> {
        val toUrl = url.toUrl()
        return DefaultClient.delete(toUrl.cPort(), toUrl.host, url)
    }

    /**
     * 创建一个 PUT 请求
     * 并在创建前检测是否支持 SSL(来自于 isHeadSSL 字段设置)
     */
    fun createPut(uri: URI): HttpRequest<Buffer> = DefaultClient.put(uri.toString()).apply {
        runCatching { ssl(uri.toURL().isSSl()) }
    }

    /**
     * 创建一个 PUT 请求
     * 并在创建前检测是否支持 SSL(来自于 isHeadSSL 字段设置)
     */
    fun createPut(url: String): HttpRequest<Buffer> {
        val toUrl = url.toUrl()
        return DefaultClient.put(toUrl.cPort(), toUrl.host, url)
    }

    /**
     * 创建一个 HEAD 请求
     * 并在创建前检测是否支持 SSL(来自于 isHeadSSL 字段设置)
     */
    fun createHead(uri: URI): HttpRequest<Buffer> = DefaultClient.head(uri.toString())

    /**
     * 创建一个 HEAD 请求
     * 并在创建前检测是否支持 SSL(来自于 isHeadSSL 字段设置)
     */
    fun createHead(url: String): HttpRequest<Buffer> {
        val toUrl = url.toUrl()
        return DefaultClient.head(toUrl.cPort(), toUrl.host, url)
    }

    /**
     * 创建一个 GET 请求
     * 并在创建前检测是否支持 SSL(来自于 isHeadSSL 字段设置)
     *
     */
    fun get(url: String, headers: HashMap<String, String> = HashMap()): HttpResponse<Buffer> {
        return createGet(url).putHeaders(headers.toHeaders()).send().awaitToCompleteExceptionally()
    }

    /**
     * 创建一个 POST 请求
     * 并在创建前检测是否支持 SSL(来自于 isHeadSSL 字段设置)
     */
    fun post(url: String, headers: HashMap<String, String> = HashMap()): HttpResponse<Buffer> {
        return createPost(url).putHeaders(headers.toHeaders()).send().awaitToCompleteExceptionally()
    }

    /**
     * 创建一个 POST 请求
     * 并在创建前检测是否支持 SSL(来自于 isHeadSSL 字段设置)
     * @param body 请求体
     *
     */
    fun post(url: String, body: String, headers: HashMap<String, String> = HashMap()): HttpResponse<Buffer> {
        return createPost(url).putHeaders(headers.toHeaders()).sendBuffer(Buffer.buffer(body))
            .awaitToCompleteExceptionally()
    }

    /**
     * 创建一个 POST 请求。
     * 并在创建前检测是否支持 SSL(来自于 isHeadSSL 字段设置)
     * @param body 请求体
     */
    fun postJson(url: String, body: Any, headers: HashMap<String, String> = HashMap()): HttpResponse<Buffer> {
        return createPost(url).putHeaders(headers.toHeaders()).sendJson(body).awaitToCompleteExceptionally()
    }

    /**
     * 创建一个 POST 请求。
     * 并在创建前检测是否支持 SSL(来自于 isHeadSSL 字段设置)
     * @param body 请求体
     */
    fun postJsonObject(
        url: String,
        body: JsonObject,
        headers: HashMap<String, String> = HashMap(),
    ): HttpResponse<Buffer> {
        return createPost(url).putHeaders(headers.toHeaders()).sendJsonObject(body).awaitToCompleteExceptionally()
    }


    fun close() {
        logger.warn("The global HTTP client has been closed and cannot be opened again")
        DefaultClient.close()
    }

    private fun URL.cPort(): Int {
        if (port == -1) {
            return DefaultPorts.getOrDefault(protocol, options.defaultPort)
        }
        return port
    }

    private fun URL.isSSl(): Boolean {
        //如果开启了 headSSL 对 服务器是否支持 SSL 进行检测
        //作为补充手段，如果对于以下协议，无法检测到是否支持 SSL，则进行 head 检测
        if (isHeadSSL && (protocol != "https" || protocol != "ftps" || protocol != "ftp" || protocol != "http")) {
            return try {
                createHead(this.toString()).ssl(true).send().awaitToCompleteExceptionally().statusCode()
                true
            } catch (e: Exception) {
                false
            }
        }
        return protocol == "https" || protocol == "ftps"
    }


}

/**
 * 将一个包含请求头的 Map 转换为 HeadersMultiMap·
 */
fun HashMap<String, String>.toHeaders(): HeadersMultiMap {
    val map = this
    return HeadersMultiMap().apply {
        map.forEach { (key, value) ->
            this[key] = value
        }
    }
}


/**
 * 为URI里面的占位符添加参数
 */
fun HttpRequest<Buffer>.addRestfulParam(paramMap: Map<String, Any>): HttpRequest<Buffer> {
    uri(constructRestfulUrl(this.uri(), paramMap))
    return this
}

/**
 * 为URI里面的占位符添加参数
 */
fun HttpRequest<Buffer>.addRestfulParam(vararg paramMap: Any): HttpRequest<Buffer> {
    uri(constructRestfulUrl(this.uri(), *paramMap))
    return this
}

/**
 * 为URI里面的占位符添加参数
 */
fun HttpRequest<Buffer>.setRestfulParam(vararg paramMap: Any): HttpRequest<Buffer> {
    uri(constructRestfulUrl(this.uri(), *paramMap))
    return this
}

/**
 * 为URI添加查询参数
 */
fun HttpRequest<Buffer>.addQueryParams(paramMap: Map<String, String>): HttpRequest<Buffer> {
    paramMap.forEach { k, v ->
        addQueryParam(k, v)
    }
    return this
}

/**
 * 为URI添加查询参数
 */
fun HttpRequest<Buffer>.addQueryParams(pair: Pair<String, String>): HttpRequest<Buffer> {
    mapOf(pair).forEach { k, v ->
        addQueryParam(k, v)
    }
    return this
}


/**
 * 通过将给定的基础URL中的占位符使用提供的参数映射中的值进行替换，构造一个RESTful URL。
 *
 * @param baseUrl 包含占位符（格式为"{$paramName}"）的基础URL模板。
 * @param paramMap 包含要在URL中替换的参数名称和相应值的映射。
 * @return 替换占位符后的构造RESTful URL。
 */
fun constructRestfulUrl(baseUrl: String, paramMap: Map<String, Any>): String {
    var restfulUrl = baseUrl

    // 替换URL中的占位符
    for ((paramName, paramValue) in paramMap) {
        val placeholder = "{$paramName}"
        restfulUrl = restfulUrl.replace(placeholder, paramValue.toString(), true)
    }

    return restfulUrl
}

/**
 * 通过将给定URL模板中的占位符使用提供的值数组进行替换，构造一个RESTful URL。
 *
 * @param urlTemplate 包含占位符（格式为"{param}"）的URL模板。
 * @param arr 与URL模板中的占位符相位置对应的值数组。如果数组的长度小于占位符的个数，则使用不进行替换
 * @return 替换占位符后的构造RESTful URL。
 */
fun constructRestfulUrl(urlTemplate: String, vararg arr: Any): String {
    val regex = "\\{.*?}".toRegex()
    var temp = urlTemplate
    for ((index, result) in regex.findAll(urlTemplate).withIndex()) {
        val matchedText = result.value
        if (index >= arr.size) break
        temp = temp.replace(matchedText, arr[index].toString())
    }

    return temp
}
