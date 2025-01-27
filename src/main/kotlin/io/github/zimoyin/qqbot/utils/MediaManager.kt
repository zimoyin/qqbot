package io.github.zimoyin.qqbot.utils

import io.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import io.github.zimoyin.qqbot.LocalLogger
import io.github.zimoyin.qqbot.SystemLogger
import io.github.zimoyin.qqbot.net.bean.message.send.MediaMessageBean
import io.github.zimoyin.qqbot.net.http.api.API
import java.io.Serializable
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

/**
 * 用于管理已经上传的资源
 * @author : zimo
 * @date : 2024/11/26
 */
class MediaManager : ConcurrentHashMap<String, MediaMessageBean>(), Serializable {
    private val updateTime = ConcurrentHashMap<String, Long>()
    private var timeID: Long = -1
    private val logger = LocalLogger(this)

    companion object {
        /**
         * 是否启用
         */
        @JvmStatic
        var isEnable = true
            set(value) {
                field = value
                kotlin.runCatching {
                    if (!value) {
                        instance.logger.debug("取消Vertx定时任务 : ${instance.timeID}")
                        GLOBAL_VERTX_INSTANCE.cancelTimer(instance.timeID)
                        instance.timeID = -1
                    }
                    if (value) instance.timeID = GLOBAL_VERTX_INSTANCE.setPeriodic(1000 * 60 * 30) {
                        kotlin.runCatching {
                            val toRemove = mutableListOf<String>()
                            synchronized(instance.updateTime) {
                                instance.logger.debug("检查Vertx定时任务 : ${LocalDateTime.now()}")
                                for ((key, updateTime) in instance.updateTime) {
                                    if (System.currentTimeMillis() - updateTime > 1000 * 60 * 5) { // 如果超过5分钟
                                        toRemove.add(key)
                                    }
                                }

                                // 移除过期的项
                                for (key in toRemove) {
                                    instance.remove(key)
                                    instance.updateTime.remove(key)
                                }
                            }
                        }.onFailure {
                            SystemLogger.warn("检查过期Vertx定时任务失败", it)
                        }
                    }
                }.onFailure {
                    SystemLogger.warn("创建一个检查过期的Vertx定时任务失败", it)
                }
            }

        @JvmStatic
        var instance = MediaManager()
    }

    operator fun set(key: String, value: MediaMessageBean): MediaMessageBean? {
        if (API.isDebug) logger.debug("将文件缓存 : $key")
        return super.put(key, value)
    }

    override fun get(key: String): MediaMessageBean? {
        val media = super.get(key) ?: return null
        if (media.isExpired()) {
            if (API.isDebug) logger.debug("文件缓存已过期 : $key")
            remove(key)
            return null
        }
        updateTime[key] = System.currentTimeMillis()
        return media
    }

    override fun remove(key: String, value: MediaMessageBean): Boolean {
        updateTime.remove(key)
        return super.remove(key, value)
    }

    override fun remove(key: String): MediaMessageBean? {
        updateTime.remove(key)
        return super.remove(key)
    }

    override fun clear() {
        updateTime.clear()
        super.clear()
    }
}
