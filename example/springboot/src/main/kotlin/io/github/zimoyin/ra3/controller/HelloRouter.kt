package io.github.zimoyin.ra3.controller

import io.github.zimoyin.qqbot.bot.message.type.ImageMessage
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import io.github.zimoyin.qqbot.utils.ex.toBase64
import io.github.zimoyin.qqbot.utils.ex.toUrl
import io.github.zimoyin.qqbot.utils.ex.writeToText
import io.github.zimoyin.qqbot.utils.io
import io.github.zimoyin.ra3.annotations.*
import io.github.zimoyin.ra3.event.VirtualMessageEvent
import io.github.zimoyin.ra3.expand.toChainMessage
import io.github.zimoyin.ra3.expand.toPlainTextMessage
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.Router
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.delay
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

    /**
     * 注册路由
     * 正则路由以 ^ 开始
     *
     * 方法参数可以是 routingContext 或者 router 或者 routingContext 内的任何东西。以及其他的任何东西，或者 bean
     *
     */
    @RouterGet("/test/send_message/:message")
    fun send(response: HttpServerResponse, request: HttpServerRequest) {
        response.setChunked(true)
        request.bodyHandler {
            VirtualMessageEvent.create("/${request.getParam("message")}".toPlainTextMessage().toChainMessage())
            response.end()
        }
    }

    @PostConstruct
    fun init() {
        GlobalEventBus.debugLogger = true
    }
}