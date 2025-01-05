package io.github.zimoyin.ra3.service

import io.github.zimoyin.ra3.entity.PlayerCar
import io.github.zimoyin.ra3.expand.KtWrappers
import io.github.zimoyin.ra3.mapper.PlayerCarMapper
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

/**
 *
 * @author : zimo
 * @date : 2025/01/05
 */
interface IPlayerCarService {
    fun getPlayerCar(uid: String): PlayerCar?
    fun savePlayerCar(playerCar: PlayerCar): Boolean
    fun updatePlayerCar(playerCar: PlayerCar): Int
}

@Service
class PlayerCarService(
    val mapper: PlayerCarMapper
) : IPlayerCarService {


    @CachePut(value = ["playerCars"], key = "#uid")
    override fun getPlayerCar(uid: String): PlayerCar {
        return mapper.selectOne(KtWrappers.lambdaQuery<PlayerCar>().eq(PlayerCar::uid, uid))
    }

    @CachePut(value = ["playerCars"], key = "#playerCar.uid")
    override fun savePlayerCar(playerCar: PlayerCar): Boolean {
        return mapper.insertOrUpdate(playerCar)
    }

    @CacheEvict(value = ["playerCars"], key = "#playerCar.uid")
    override fun updatePlayerCar(playerCar: PlayerCar): Int {
        return mapper.updateById(playerCar)
    }
}