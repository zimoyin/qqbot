package io.github.zimoyin.ra3.config

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.PerformanceMonitorInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.io.File
import javax.sql.DataSource


/**
 *
 * @author : zimo
 * @date : 2025/01/04
 */
@Component
class SqliteConfig {
    private val logger = LoggerFactory.getLogger(SqliteConfig::class.java)

    @Value("\${spring.datasource.url}")
    private lateinit var url: String

    @Autowired
    private lateinit var dataSource: DataSource

    private fun getPath(): String {
        return url.split("?")
            .first { it.isNotEmpty() }
            .split(":")
            .last { it.isNotEmpty() }
    }

    @PostConstruct
    fun initializeDatabase() {
        try {
            val file = File(getPath()).apply {
                logger.info("数据库文件路径: $this")
                this.parentFile.mkdirs()
            }

            if (file.exists()) {
                logger.info("数据库文件已存在，无需初始化")
                return
            }

            dataSource.connection.use { connection ->
                val sql = kotlin.runCatching {
                    kotlin.runCatching { ClassPathResource("./data.sql").inputStream.reader().readText() }.getOrNull()?:
                    kotlin.runCatching { File("./data.sql").readText() }.getOrNull()?:
                    kotlin.runCatching { File("./data/data.sql").readText() }.getOrNull()
                }.getOrNull()

                if (sql.isNullOrEmpty()) {
                    logger.error("无法读取初始化 SQL 文件")
                    return@use
                }

                connection.createStatement().use { statement ->
                    statement.executeLargeUpdate(sql)
                }.let {
                    logger.info("数据库初始化成功")
                }
            }
        } catch (e: Exception) {
            logger.error("数据库初始化失败", e)
        }
    }
}