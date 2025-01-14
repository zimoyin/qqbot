package io.github.zimoyin.qqbot

import io.vertx.core.logging.VertxLoggerFormatter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.logging.*

object SystemLogger : LocalLogger("System")


open class LocalLogger(name: String) {
    constructor(clazz: Class<*>) : this(clazz.name)

    companion object {
        init {
            changeJULogging { VertxLoggerFormatter().format(it) }
        }
        /**
         * 判断项目中是否有slf4j的实现类
         */
        @JvmStatic
        fun isSlf4jImplClassExists(): Boolean {
            return try {
                Class.forName("org.slf4j.impl.StaticLoggerBinder")
                true
            } catch (e: ClassNotFoundException) {
                false
            }
        }

        @JvmStatic
        @JvmOverloads
        fun changeJULogging(
            // 日志级别
            level: Level = Level.INFO,
            // 获取根记录器
            rootLogger: java.util.logging.Logger = java.util.logging.Logger.getLogger(""),
            simpleFormatter: (LogRecord) -> String = {record->
                String.format(
                    "%1\$tF %1\$tT [%4\$s] %5\$s %n",
                    Date(record.millis),
                    record.sourceClassName,
                    record.sourceMethodName,
                    record.level.name,
                    record.message
                )
            },
        ) {
            // 设置全局日志级别
            rootLogger.setLevel(level)

            // 获取控制台处理器
            var consoleHandler: Handler? = null
            for (handler in rootLogger.handlers) {
                if (handler is ConsoleHandler) {
                    consoleHandler = handler
                    break
                }
            }

            if (consoleHandler != null) {
                // 设置控制台处理器的日志级别
                consoleHandler.level = level

                // 设置自定义的格式化器
                consoleHandler.formatter = object : SimpleFormatter() {
                    @Synchronized
                    override fun format(record: LogRecord): String {
                        return simpleFormatter(record)
                    }
                }
            }
        }
    }

    private val SystemLogger1: Logger? by lazy {
        if (isSlf4jImplClassExists()) LoggerFactory.getLogger(name) else null
    }

    private val SystemLogger2: io.vertx.core.impl.logging.Logger by lazy {
        io.vertx.core.impl.logging.LoggerFactory.getLogger(name)
    }

    @JvmOverloads
    fun error(message: String = "", throwable: Throwable? = null) {
        if (SystemLogger1 != null) {
            SystemLogger1!!.error(message, throwable)
        } else {
            SystemLogger2.error(message, throwable)
        }
    }

    @JvmOverloads
    fun warn(message: String, throwable: Throwable? = null) {
        if (SystemLogger1 != null) {
            SystemLogger1!!.warn(message, throwable)
        } else {
            SystemLogger2.warn(message, throwable)
        }
    }

    @JvmOverloads
    fun info(message: String, throwable: Throwable? = null) {
        if (SystemLogger1 != null) {
            SystemLogger1!!.info(message, throwable)
        } else {
            SystemLogger2.info(message, throwable)
        }
    }

    @JvmOverloads
    fun debug(message: String, throwable: Throwable? = null) {
        if (SystemLogger1 != null) {
            SystemLogger1!!.debug(message, throwable)
        } else {
            SystemLogger2.debug(message, throwable)
        }
    }
}
