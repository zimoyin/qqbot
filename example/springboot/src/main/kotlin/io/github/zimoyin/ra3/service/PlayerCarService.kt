package io.github.zimoyin.ra3.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import io.github.zimoyin.ra3.aspect.RegisterAOP
import io.github.zimoyin.ra3.entity.PlayerCar
import io.github.zimoyin.ra3.expand.KtWrappers
import io.github.zimoyin.ra3.mapper.PlayerCarMapper
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.stereotype.Service

/**
 *
 * @author : zimo
 * @date : 2025/01/05
 */
interface IPlayerCarService {
    fun getPlayerCar(uid: String): PlayerCar
    fun savePlayerCar(playerCar: PlayerCar): Boolean
    fun updatePlayerCar(playerCar: PlayerCar): Int
    fun egress(uid: String): Boolean
    fun create(uid: String): Boolean
    fun returned(uid: String): Long
    fun isEgress(uid: String): Boolean
    fun exists(uid: String): Boolean
}

@Service
class PlayerCarService(
    val mapper: PlayerCarMapper
) : IPlayerCarService {


    @RegisterAOP
    @CachePut(value = ["playerCars"], key = "#uid")
    override fun getPlayerCar(uid: String): PlayerCar {
        return mapper.selectOne(KtWrappers.lambdaQuery<PlayerCar>().eq(PlayerCar::uid, uid))
            ?: throw NullPointerException(
                "$uid player car not found"
            )
    }

    @CacheEvict(value = ["playerCars"], key = "#playerCar.uid")
    override fun savePlayerCar(playerCar: PlayerCar): Boolean {
        if (playerCar.level > 5) throw IllegalArgumentException("level must be 0-5")
        return mapper.insertOrUpdate(playerCar)
    }

    @CacheEvict(value = ["playerCars"], key = "#playerCar.uid")
    override fun updatePlayerCar(playerCar: PlayerCar): Int {
        if (playerCar.level > 5) throw IllegalArgumentException("level must be 0-5")
        return mapper.updateById(playerCar)
    }

    @CacheEvict(value = ["playerCars"], key = "#uid")
    override fun egress(uid: String): Boolean {
        val car = getPlayerCar(uid)
        car.egressTimes = System.currentTimeMillis()
        return updatePlayerCar(car) > 0
    }

    override fun create(uid: String): Boolean {
        return savePlayerCar(PlayerCar(uid))
    }

    @CacheEvict(value = ["playerCars"], key = "#uid")
    override fun returned(uid: String): Long {
        val car = getPlayerCar(uid)
        val egressTimes = car.egressTimes
        car.egressTimes = -1
        updatePlayerCar(car)
        return egressTimes
    }

    override fun isEgress(uid: String): Boolean {
        val car = getPlayerCar(uid)
        return car.egressTimes <= -1
    }

    override fun exists(uid: String): Boolean {
        return mapper.exists(KtQueryWrapper(PlayerCar::class.java).eq(PlayerCar::uid, uid))
//        return mapper.exists(KtWrappers.lambdaQuery<PlayerCar>().eq(PlayerCar::uid, uid))
    }
}