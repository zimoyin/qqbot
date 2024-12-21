package com.github.zimoyin.qqbot

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object SystemLogger : LocalLogger("System")


open class LocalLogger(name: String) {
    constructor(clazz: Class<*>) : this(clazz.name)

    private val SystemLogger1: Logger? by lazy {
        if (isSlf4jImplClassExists()) LoggerFactory.getLogger(name) else null
    }

    /**
     * 判断项目中是否有slf4j的实现类
     */
    private fun isSlf4jImplClassExists(): Boolean {
        return try {
            Class.forName("org.slf4j.impl.StaticLoggerBinder")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    private val SystemLogger2: io.vertx.core.impl.logging.Logger by lazy {
        io.vertx.core.impl.logging.LoggerFactory.getLogger(name)
    }

    fun error(message: String, throwable: Throwable? = null) {
        if (SystemLogger1 != null) {
            SystemLogger1!!.error(message, throwable)
        } else {
            SystemLogger2.error(message, throwable)
        }
    }

    fun warn(message: String, throwable: Throwable? = null) {
        if (SystemLogger1 != null) {
            SystemLogger1!!.warn(message, throwable)
        } else {
            SystemLogger2.warn(message, throwable)
        }
    }

    fun info(message: String, throwable: Throwable? = null) {
        if (SystemLogger1 != null) {
            SystemLogger1!!.info(message, throwable)
        } else {
            SystemLogger2.info(message, throwable)
        }
    }

    fun debug(message: String, throwable: Throwable? = null) {
        if (SystemLogger1 != null) {
            SystemLogger1!!.debug(message, throwable)
        } else {
            SystemLogger2.debug(message, throwable)
        }
    }
}
