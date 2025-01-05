package io.github.zimoyin.ra3.controller

import io.github.zimoyin.ra3.annotations.AutoClose
import io.github.zimoyin.ra3.annotations.Rout
import io.github.zimoyin.ra3.annotations.RouterController
import io.github.zimoyin.ra3.service.IRegisterService
import io.vertx.core.Context
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.kotlin.core.json.jsonObjectOf
import org.bouncycastle.asn1.x500.style.RFC4519Style.uid
import org.slf4j.LoggerFactory
import java.net.URLDecoder

/**
 *
 * @author : zimo
 * @date : 2025/01/04
 */
@RouterController
class RegisterRouter(
    val service: IRegisterService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Rout("/register/:id/:name/:cid")
    @AutoClose
    fun register(response: HttpServerResponse, request: HttpServerRequest) {
        val id = request.getParam("id")
        val name = request.getParam("name")
        val cid = request.getParam("cid")
        kotlin.runCatching {
            service.register(id, name, cid.toInt())
            response.end(jsonObjectOf("register" to true).toString())
        }.onFailure {
            response.statusCode = 500
            response.end(jsonObjectOf("register" to false,"message" to it.message).toString())
            logger.error("注册失败", it)
        }
    }
}