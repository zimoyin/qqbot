package com.github.zimoyin.qqbot

import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val SystemLogger: Logger by lazy {
    LoggerFactory.getLogger("System")
}

/**
 * 全局 vertx 实例配置
 * 他将在被调用的时候被初始化，你应该在 GLOBAL_VERTX_INSTANCE 初始化之前完成他的修改
 */
val GLOBAL_VERTX_OPTIONS: VertxOptions by lazy {
    VertxOptions().apply {
        setWorkerPoolSize(6)
        setEventLoopPoolSize(3)
        setInternalBlockingPoolSize(12)
        setHAEnabled(true)
        SystemLogger.debug("已完成一个全局的Vertx 实例的配置 : {}", this)
    }
}

/**
 * 全局单例 vertx 注意如果你需要使用多个 vertx 实例，请自行组织
 */
val GLOBAL_VERTX_INSTANCE: Vertx by lazy {
    val options = GLOBAL_VERTX_OPTIONS
    //集群判断，如果有集群配置就创建当前应用中的用于集群的 vertx
    if (options.clusterManager == null) {
        SystemLogger.info("已创建一个全局的单机 Vertx 实例")
        Vertx.vertx(options).exceptionHandler {
            SystemLogger.error("Global Vertx Exception", it)
        }
    } else {
        SystemLogger.info("已创建一个全局的集群 Vertx 实例")
        Vertx.clusteredVertx(options).toCompletionStage().toCompletableFuture().get().exceptionHandler {
            SystemLogger.error("Global Vertx Exception", it)
        }
    }
}

fun createVertx(options: VertxOptions = GLOBAL_VERTX_OPTIONS): Vertx {
    return Vertx.vertx(options)
}

const val VERSION = "alpha 1.0.0"
const val VERTX_VERSION = "4.5.0"

fun main() {
    println("Hello QQ_Bot_Framework!")
    println("Date: ${System.currentTimeMillis()}")
    println("Version: $VERSION")
    println("Vertx version: $VERTX_VERSION")
    println("JVM version: ${System.getProperty("java.version")}")
}