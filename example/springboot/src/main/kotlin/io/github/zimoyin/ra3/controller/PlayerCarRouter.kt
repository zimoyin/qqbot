package io.github.zimoyin.ra3.controller

import io.github.zimoyin.ra3.annotations.AutoClose
import io.github.zimoyin.ra3.annotations.Rout
import io.github.zimoyin.ra3.annotations.RouterController
import io.github.zimoyin.ra3.entity.PlayerCar
import io.github.zimoyin.ra3.expand.KtWrappers
import io.github.zimoyin.ra3.mapper.PlayerCarMapper
import io.github.zimoyin.ra3.service.PlayerCarService
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 *
 * @author : zimo
 * @date : 2025/01/05
 */
@RouterController
class PlayerCarRouter (
    val service: PlayerCarService
){
    @Rout("/player_car/get/:uid")
    @AutoClose
    fun getPlayerCar(response: HttpServerResponse, request: HttpServerRequest): PlayerCar {
        val uid = request.getParam("uid")
        return service.getPlayerCar(uid)
    }

    @Rout("/player_car/save/:uid")
    @AutoClose
    fun savePlayerCar(response: HttpServerResponse, request: HttpServerRequest): Boolean {
        val uid = request.getParam("uid")
        val playerCar = PlayerCar(uid)
        return service.savePlayerCar(playerCar)
    }
}