package io.github.zimoyin.qqbot

import io.github.zimoyin.qqbot.utils.io
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.URL
import java.time.LocalDateTime
import java.util.*

/**
 * 全局 vertx 实例配置
 * 他将在被调用的时候被初始化，你应该在 GLOBAL_VERTX_INSTANCE 初始化之前完成他的修改
 */
val GLOBAL_VERTX_OPTIONS: VertxOptions by lazy {
    Config.GLOBAL_VERTX_OPTIONS
}

/**
 * 全局单例 vertx 注意如果你需要使用多个 vertx 实例，请自行组织
 */
val GLOBAL_VERTX_INSTANCE: Vertx by lazy {
    Config.GLOBAL_VERTX_INSTANCE
}

object Config {
    /**
     * 全局 vertx 实例配置
     * 他将在被调用的时候被初始化，你应该在 GLOBAL_VERTX_INSTANCE 初始化之前完成他的修改
     */
    @JvmStatic
    val GLOBAL_VERTX_OPTIONS: VertxOptions by lazy {
        val vertxOptions = VertxOptions()
        vertxOptions.apply {
            setWorkerPoolSize(12) // VertxOptions.DEFAULT_WORKER_POOL_SIZE
            setEventLoopPoolSize(VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE)
            setInternalBlockingPoolSize(VertxOptions.DEFAULT_INTERNAL_BLOCKING_POOL_SIZE)
            setHAEnabled(true)
            io {
                SystemLogger.debug("[异步日志][${LocalDateTime.now()}]已完成一个全局的Vertx 实例的配置 : $this")
            }
        }
    }

    /**
     * 全局单例 vertx 注意如果你需要使用多个 vertx 实例，请自行组织
     */
    @JvmStatic
    val GLOBAL_VERTX_INSTANCE: Vertx by lazy {
        val options = GLOBAL_VERTX_OPTIONS
        //集群判断，如果有集群配置就创建当前应用中的用于集群的 vertx
        if (options.clusterManager == null) {
            SystemLogger.info("创建一个全局的单机 Vertx 实例")
            Vertx.vertx(options).exceptionHandler {
                SystemLogger.error("Global Vertx Exception", it)
            }
        } else {
            SystemLogger.info("创建一个全局的集群 Vertx 实例")
            Vertx.clusteredVertx(options).toCompletionStage().toCompletableFuture().get().exceptionHandler {
                SystemLogger.error("Global Vertx Exception", it)
            }
        }
    }

    @JvmStatic
    fun createVertx(options: VertxOptions = GLOBAL_VERTX_OPTIONS): Vertx {
        return Vertx.vertx(options)
    }


}
