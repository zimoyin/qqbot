package com.github.zimoyin.qqbot.net.http

import com.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import com.github.zimoyin.qqbot.utils.ex.awaitToCompleteExceptionally
import com.github.zimoyin.qqbot.utils.ex.toUrl
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.impl.headers.HeadersMultiMap
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.HttpRequest
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.URL


/**
 * 创建一个专用于与腾讯服务器通信的HttpClient
 */
object DefaultHttpClient {
    val defaultPorts: Map<String, Int> = HashMap<String, Int>().apply {
        put("http", 80)
        put("https", 443)
        put("ftp", 21)
        put("ssh", 22)
        put("smtp", 25)
        put("pop3", 110)
    }

    val DefaultHeaders by lazy {
        HeadersMultiMap().apply {
            //通用头
        }
    }
    private val logger: Logger by lazy { LoggerFactory.getLogger(DefaultHttpClient::class.java) }

    /**
     * 使用 head 查看服务器是否支持 SSL
     */
    var headSSL = true
    private val options: WebClientOptions by lazy {
        WebClientOptions()
            .setUserAgent("java_qqbot_gf:0.0.1")
            .setDefaultHost("api.sgroup.qq.com")
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
            .setMaxWaitQueueSize(-1)
    }

    val client: WebClient by lazy {
        WebClient.create(GLOBAL_VERTX_INSTANCE, options)
    }

    fun createGet(uri: URI): HttpRequest<Buffer> = client.get(uri.toString())
    fun createGet(url: String): HttpRequest<Buffer> {
        val toUrl = url.toUrl()
        return client.get(toUrl.cPort(), toUrl.host, toUrl.path).ssl(toUrl.isSSl())
    }

    fun createPost(uri: URI): HttpRequest<Buffer> = client.post(uri.toString())
    fun createPost(url: String): HttpRequest<Buffer> {
        val toUrl = url.toUrl()
        return client.post(toUrl.cPort(), toUrl.host, toUrl.path).ssl(toUrl.isSSl()).followRedirects(true)
    }

    fun createRequest(method: HttpMethod, uri: URI): HttpRequest<Buffer> = client.request(method, uri.toString())
    fun createRequest(method: HttpMethod, url: String): HttpRequest<Buffer> {
        return client.request(method, url.toUrl().cPort(), url.toUrl().host, url.toUrl().path).ssl(url.toUrl().isSSl())
    }

    fun createDelete(uri: URI): HttpRequest<Buffer> = client.delete(uri.toString())
    fun createDelete(url: String): HttpRequest<Buffer> {
        val toUrl = url.toUrl()
        return client.delete(toUrl.cPort(), toUrl.host, toUrl.path)
    }

    fun createPut(uri: URI): HttpRequest<Buffer> = client.put(uri.toString())
    fun createPut(url: String): HttpRequest<Buffer> {
        val toUrl = url.toUrl()
        return client.put(toUrl.cPort(), toUrl.host, toUrl.path)
    }

    fun createHead(uri: URI): HttpRequest<Buffer> = client.head(uri.toString())
    fun createHead(url: String): HttpRequest<Buffer> {
        val toUrl = url.toUrl()
        return client.head(toUrl.cPort(), toUrl.host, toUrl.path)
    }


    fun get(url: String, headers: HashMap<String, String> = HashMap()): HttpResponse<Buffer> {
        return createGet(url).putHeaders(headers.toHeaders()).send().awaitToCompleteExceptionally()
    }

    fun post(url: String, headers: HashMap<String, String> = HashMap()): HttpResponse<Buffer> {
        return createPost(url).putHeaders(headers.toHeaders()).send().awaitToCompleteExceptionally()
    }

    fun post(url: String, body: String, headers: HashMap<String, String> = HashMap()): HttpResponse<Buffer> {
        return createPost(url).putHeaders(headers.toHeaders()).sendBuffer(Buffer.buffer(body))
            .awaitToCompleteExceptionally()
    }

    fun postJson(url: String, body: Any, headers: HashMap<String, String> = HashMap()): HttpResponse<Buffer> {
        return createPost(url).putHeaders(headers.toHeaders()).sendJson(body).awaitToCompleteExceptionally()
    }

    fun postJsonObject(
        url: String,
        body: JsonObject,
        headers: HashMap<String, String> = HashMap(),
    ): HttpResponse<Buffer> {
        return createPost(url).putHeaders(headers.toHeaders()).sendJsonObject(body).awaitToCompleteExceptionally()
    }


    fun close() {
        logger.warn("The global HTTP client has been closed and cannot be opened again")
        client.close()
    }

    private fun URL.cPort(): Int {
        if (port == -1) {
            return defaultPorts.getOrDefault(protocol, options.defaultPort)
        }
        return port
    }

    private fun URL.isSSl(): Boolean {
        //如果开启了 headSSL 对 服务器是否支持 SSL 进行检测
        //作为补充手段，如果对于以下协议，无法检测到是否支持 SSL，则进行 head 检测
        if (headSSL && (protocol != "https" || protocol != "ftps" || protocol != "ftp" || protocol != "http")) {
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