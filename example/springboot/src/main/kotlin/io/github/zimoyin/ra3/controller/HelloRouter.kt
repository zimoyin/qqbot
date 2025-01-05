package io.github.zimoyin.ra3.controller

import io.github.zimoyin.qqbot.utils.ex.writeToText
import io.github.zimoyin.ra3.annotations.Rout
import io.github.zimoyin.ra3.annotations.RouterController
import io.github.zimoyin.ra3.annotations.RouterGet
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.Router
import org.springframework.stereotype.Component

/**
 *
 * @author : zimo
 * @date : 2025/01/04
 */
@RouterController
class HelloRouter {


    /**
     * 注册路由
     * 正则路由以 ^ 开始
     *
     * 方法参数可以是 routingContext 或者 router 或者 routingContext 内的任何东西。以及其他的任何东西，或者 bean
     *
     */
    @Rout("/hello")
//    @RouterGet
    fun hello(response: HttpServerResponse, request: HttpServerRequest) {
        request.bodyHandler {
            println("浏览器发送的数据：" + it.writeToText())
            response.end("hello world")
        }
    }
}