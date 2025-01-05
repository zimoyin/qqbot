package io.github.zimoyin.ra3.framework

import io.github.zimoyin.qqbot.utils.ex.toJsonObject
import io.github.zimoyin.ra3.ApplicationStart
import io.github.zimoyin.ra3.annotations.*
import io.github.zimoyin.ra3.expand.getBeanByName
import io.vertx.ext.web.RoutingContext
import jakarta.annotation.PostConstruct
import org.aspectj.weaver.tools.cache.SimpleCacheFactory.path
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import java.sql.Connection
import javax.swing.text.html.HTML.Tag.P

/**
 *
 * @author : zimo
 * @date : 2025/01/04
 */
@Component
class RouterProcessor(val applicationContext: ApplicationContext) {

    private lateinit var router: io.vertx.ext.web.Router
    private val logger = LoggerFactory.getLogger(RouterProcessor::class.java)

    @Autowired
    private lateinit var start: ApplicationStart

    @EventListener(ApplicationReadyEvent::class)
    fun init(event: ApplicationReadyEvent) {
        router = applicationContext.getBeanByName("router")
        for (bean in initializedBeanInstances) {
            registerBean(bean)
        }
    }

    /**
     * 初始化完成后的 bean 列表
     */
    private val initializedBeanInstances by lazy {
        applicationContext.beanDefinitionNames.filter {
            it != this.javaClass.simpleName &&
                    it != CommanderProcessor::class.simpleName &&
                    it != EventProcessor::class.simpleName &&
                    it != Connection::class.simpleName
        }.mapNotNull {
            applicationContext.getBean(it)
        }
    }

    fun registerBean(bean: Any) {
        val clazz = bean.javaClass

        for (method in clazz.methods) {
            runCatch {
                registerMethod(method, bean)
            }
        }
    }

    fun registerMethod(method: Method, bean: Any) {
        if (method.isAnnotationPresent(Router::class.java)) {
            val path = method.getAnnotation(Router::class.java).path
            router.let {
                if (path.startsWith("^")) it.routeWithRegex(path.replaceFirst("^", ""))
                else it.route(path)
            }.order(0).handler {
                invoke(method, bean, it)
            }
            return
        }
        if (method.isAnnotationPresent(Rout::class.java)) {
            val path = method.getAnnotation(Rout::class.java).path
            router.let {
                if (path.startsWith("^")) it.routeWithRegex(path.replaceFirst("^", ""))
                else it.route(path)
            }.order(0).handler {
                invoke(method, bean, it)
            }
            return
        }

        if (method.isAnnotationPresent(RouterPost::class.java)) {
            val path = method.getAnnotation(RouterPost::class.java).path
            router.let {
                if (path.startsWith("^")) it.postWithRegex(path.replaceFirst("^", ""))
                else it.post(path)
            }.order(0).handler {
                invoke(method, bean, it)
            }
            return
        }

        if (method.isAnnotationPresent(RouterGet::class.java)) {
            val path = method.getAnnotation(RouterGet::class.java).path
            router.let {
                if (path.startsWith("^")) it.getWithRegex(path.replaceFirst("^", ""))
                else it.get(path)
            }.order(0).handler {
                invoke(method, bean, it)
            }
            return
        }

        if (method.isAnnotationPresent(RouterPut::class.java)) {
            val path = method.getAnnotation(RouterPut::class.java).path
            router.let {
                if (path.startsWith("^")) it.putWithRegex(path.replaceFirst("^", ""))
                else it.put(path)
            }.order(0).handler {
                invoke(method, bean, it)
            }
            return
        }

        if (method.isAnnotationPresent(RouterPatch::class.java)) {
            val path = method.getAnnotation(RouterPatch::class.java).path
            router.let {
                if (path.startsWith("^")) it.patchWithRegex(path.replaceFirst("^", ""))
                else it.patch(path)
            }.order(0).handler {
                invoke(method, bean, it)
            }
            return
        }
        if (method.isAnnotationPresent(RouterPatch::class.java)) {
            val path = method.getAnnotation(RouterDelete::class.java).path
            router.let {
                if (path.startsWith("^")) it.deleteWithRegex(path.replaceFirst("^", ""))
                else it.delete(path)
            }.order(0).handler {
                invoke(method, bean, it)
            }
            return
        }
        if (method.isAnnotationPresent(RouterHead::class.java)) {
            val path = method.getAnnotation(RouterHead::class.java).path
            router.let {
                if (path.startsWith("^")) it.headWithRegex(path.replaceFirst("^", ""))
                else it.head(path)
            }.order(0).handler {
                invoke(method, bean, it)
            }
            return
        }
        if (method.isAnnotationPresent(RouterOptions::class.java)) {
            val path = method.getAnnotation(RouterOptions::class.java).path
            router.let {
                if (path.startsWith("^")) it.optionsWithRegex(path.replaceFirst("^", ""))
                else it.options(path)
            }.order(0).handler {
                invoke(method, bean, it)
            }
            return
        }
    }

    fun invoke(method: Method, bean: Any, routingContext: RoutingContext) {
        val args = arrayListOf<Any>()
        val isHasAutoClose = method.isAnnotationPresent(AutoClose::class.java)

        method.parameters.forEach {
            val bean0 = kotlin.runCatching { applicationContext.getBean(it.name, it.type) }.getOrNull()
                ?: kotlin.runCatching { applicationContext.getBean(it.type) }.getOrNull()

            if (bean0 != null) {
                args.add(bean0)
            } else {
                args.add(createParameter(it, routingContext))
            }
        }

        //执行方法
        try {
            routingContext.request().paramsCharset = "UTF-8"
            val result = method.invoke(bean, *args.toTypedArray())
            kotlin.runCatching {
                if (isHasAutoClose) {
                    val response = routingContext.response()
                    response.putHeader("content-type", "application/json")
                    if (method.returnType == Unit::class.java) {
                        response.end()
                    }
                    if (result == null) {
                        response.end()
                    }
                    if (result is String) {
                        response.end(result)
                    } else if (result is Number || result is Comparable<*>) {
                        response.end(result.toString())
                    } else {
                        kotlin.runCatching {
                            response.end(result.toJsonObject().toString())
                        }.onFailure {
                            response.end()
                            logger.debug("自动关闭连接失败", it)
                        }
                    }
                }
            }
        } catch (e: InvocationTargetException) {
            kotlin.runCatching { routingContext.response().end("Server Error!!!!") }
            logger.error("路由执行失败, $method 方法内部存在错误逻辑导致方法执行失败", e)
        } catch (e: Exception) {
            kotlin.runCatching { routingContext.response().end("Server Error!!!!") }
            logger.error("路由执行失败", e)
        }
    }

    private fun createParameter(value: Parameter, routingContext: RoutingContext): Any {
        val name = value.name
        val type = value.type

        when (name) {
            "res", "response", "resp" -> return routingContext.response()
            "req", "request", "requ" -> return routingContext.request()
            "body", "reqBody", "requestBody" -> return routingContext.body()
            "headers", "header", "reqHeader", "requestHeader", "reqHeaders", "requestHeaders" -> return routingContext
                .request()
                .headers()

            "query", "reqQuery", "requestQuery", "reqQueries", "requestQueries" -> return routingContext.queryParams()
            "data", "reqData", "requestData" -> return routingContext.data()
            "params", "reqParams", "requestParams" -> return routingContext.pathParams()
            "cookie", "reqCookie", "requestCookie" -> return routingContext.cookieMap()
            "session", "reqSession", "requestSession" -> return routingContext.session()
            "user", "reqUser", "requestUser" -> return routingContext.user()
            "bodyAsString", "reqBodyAsString", "requestBodyAsString" -> return routingContext.bodyAsString
            "bodyAsJson", "reqBodyAsJson", "requestBodyAsJson" -> return routingContext.bodyAsJson
            "bodyAsBuffer", "reqBodyAsBuffer", "requestBodyAsBuffer" -> return routingContext.body().buffer()
            "routingContext", "context", "routerContext", "routContext" -> return routingContext
            "rout", "router" -> return routingContext.currentRoute()
            "vertx", "vertxContext" -> return routingContext.vertx()
            "responseHeaders", "responseHeader" -> return routingContext.response().headers()
            "uri" -> return routingContext.request().uri()
            "absoluteURI" -> return routingContext.request().absoluteURI()
            "authority" -> return routingContext.request().authority()
            "isSSL", "ssl", "isSsl", "isSSl", "isssl", "SSL", "Ssl" -> return routingContext.request().isSSL
        }

        kotlin.runCatching {
            type.getConstructor().newInstance()
        }.onSuccess {
            return it
        }

        throw IllegalArgumentException("Unable to parse parameters：$name")
    }


    /**
     * 调用方法，并根据方法参数上的注解自动解析参数。
     *
     * @param method 要调用的方法
     * @param bean 包含要调用的方法的对象实例
     * @param routingContext Vert.x路由上下文，用于获取HTTP请求参数等信息
     */
//    fun invoke(method: Method, bean: Any, routingContext: RoutingContext) {
//        val args = ArrayList<Any>()
//
//        // 遍历方法参数，解析每个参数并添加到args列表中
//        method.parameters.forEachIndexed { index, parameter ->
//
//            args.add(paramValue)
//        }
//
//        try {
//            // 使用反射调用方法，并传入解析后的参数
//            method.invoke(bean, *args.toTypedArray())
//        } catch (e: Exception) {
//            e.printStackTrace()
//            // 处理异常（如日志记录、错误响应等）
//        }
//    }


    fun <T : Any> runCatch(block: () -> T): T? {
        try {
            return block()
        } catch (e: Exception) {
            logger.error("路由捕获到异常", e)
        }
        return null
    }
}