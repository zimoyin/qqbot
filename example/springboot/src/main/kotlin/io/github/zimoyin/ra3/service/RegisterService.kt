package io.github.zimoyin.ra3.service

import io.github.zimoyin.ra3.entity.User
import io.github.zimoyin.ra3.expand.KtWrappers
import io.github.zimoyin.ra3.mapper.UsersMapper
import org.springframework.stereotype.Service

interface IRegisterService {
    /**
     * 注册
     * @param id QQ ID
     * @param name 昵称
     * @param cid 阵营ID
     * @return 是否注册成功 ，false 代表以及存在了值
     */
    fun register(id: String, name: String, cid: Int): Boolean {
        return register(
            User(
                username = name,
                uid = id,
                cid = cid
            )
        )
    }

    /**
     * 注册
     * @param user 用户
     * @return 是否注册成功 ，false 代表已经存在了值
     */
    fun register(user: User): Boolean

    fun isRegistered(uid: String): Boolean
    fun unregister(uid: String): Int
}

/**
 *
 * @author : zimo
 * @date : 2025/01/04
 */
@Service
class RegisterService(
    val mapper: UsersMapper
) : IRegisterService {
    override fun register(user: User): Boolean {
        if (user.cid !in 1..3) throw IllegalArgumentException("Camp ID must be between 1 and 3")
        if (isRegistered(user.uid)) return false
        mapper.insert(user)
        return true
    }

    override fun isRegistered(uid: String): Boolean {
        return mapper.selectCount(KtWrappers.lambdaQuery<User>().eq(User::uid, uid)) > 0
    }

    override fun unregister(uid: String): Int {
        return mapper.delete(KtWrappers.lambdaQuery<User>().eq(User::uid, uid))
    }
}