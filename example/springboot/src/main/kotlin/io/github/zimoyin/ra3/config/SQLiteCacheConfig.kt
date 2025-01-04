package io.github.zimoyin.ra3.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.sqlite.SQLiteDataSource
import java.sql.Connection
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author : zimo
 * @date : 2025/01/04
 */
@Configuration
class SQLiteCacheConfig {

    @Value("\${spring.cache.sqlite.url:\"jdbc:sqlite::memory:\"}")
    var url: String = "jdbc:sqlite::memory:"

    @Value("\${spring.cache.sqlite.enable:false}")
    var enable: Boolean = false

    @Value("\${spring.cache.sqlite.cache-size:100}")
    var cacheSize: Int = 100

    @Value("\${spring.cache.sqlite.expiration-milliseconds:60000}")
    var expirationMilliseconds: Long = 60000L // 缓存过期时间，单位：毫秒

    @Bean
    fun sqliteMemoryConnection(): Connection {
        val dataSource = SQLiteDataSource()
        dataSource.url = url
        return dataSource.connection
    }

    @Bean("cacheManager")
    @ConditionalOnProperty(name = ["spring.cache.sqlite.enable"], havingValue = "true", matchIfMissing = false)
    fun cacheManager(sqliteMemoryConnection: Connection): CacheManager {
        return SQLiteCacheManager(sqliteMemoryConnection, cacheSize, expirationMilliseconds)
    }

    class SQLiteCacheManager(
        private val connection: Connection,
        private val maxSize: Int,
        private val expirationTime: Long
    ) : CacheManager {
        private val cacheMap: MutableMap<String, Cache> = ConcurrentHashMap()

        init {
            connection.createStatement()
                .execute(""" CREATE TABLE IF NOT EXISTS cache (key TEXT PRIMARY KEY, value BLOB, expires_at INTEGER, created_at INTEGER)""")
        }

        override fun getCache(name: String): Cache {
            return cacheMap.computeIfAbsent(name) { k -> SQLiteCache(k, connection, maxSize, expirationTime) }
        }

        override fun getCacheNames(): Collection<String> {
            return Collections.unmodifiableSet(cacheMap.keys)
        }
    }

    class SQLiteCache(
        private val name: String,
        private val connection: Connection,
        private val maxSize: Int,
        private val expirationTime: Long
    ) : Cache {

        private val lock = Any()

        override fun getName(): String = name

        override fun getNativeCache(): Any = connection

        override fun get(key: Any): Cache.ValueWrapper? {
            return getFromDatabase(key.toString())
        }

        override fun <T : Any?> get(key: Any, type: Class<T>?): T? {
            val wrapper = get(key) ?: return null
            return wrapper.get() as? T
        }

        override fun <T : Any?> get(key: Any, valueLoader: Callable<T>): T? {
            val existingValue = get(key, valueLoader.javaClass)
            if (existingValue != null) {
                return existingValue.call()
            }
            val newValue = valueLoader.call()
            put(key, newValue)
            return newValue
        }

        override fun put(key: Any, value: Any?) {
            synchronized(lock) {
                // 如果缓存数量已超过最大限制
                if (getCacheSize() >= maxSize) {
                    evictExpiredItems() // 清理过期的缓存
                    if (getCacheSize() >= maxSize) evictOldestItem() // 删除最旧的缓存
                }
                putInDatabase(key.toString(), value)
            }
        }

        override fun evict(key: Any) {
            deleteFromDatabase(key.toString())
        }

        override fun clear() {
            clearDatabase()
        }

        private fun getFromDatabase(key: String): Cache.ValueWrapper? {
            var result: Cache.ValueWrapper? = null
            connection.prepareStatement("SELECT value, expires_at, created_at FROM cache WHERE key = ?")
                .let { preparedStatement ->
                    preparedStatement.setString(1, key)
                    preparedStatement.executeQuery().let { resultSet ->
                        if (resultSet.next()) {
                            val value = resultSet.getObject(1)
                            val expiresAt = resultSet.getLong(2)
                            val createdAt = resultSet.getLong(3)
                            // 如果缓存项已经过期，返回 null
                            if (System.currentTimeMillis() > expiresAt) {
                                deleteFromDatabase(key) // 删除过期项
                            } else {
                                result = Cache.ValueWrapper { value }
                            }
                        }
                    }
                }
            return result
        }

        private fun putInDatabase(key: String, value: Any?) {
            val createdAt = System.currentTimeMillis()
            val expiresAt = createdAt + expirationTime
            connection.prepareStatement("INSERT OR REPLACE INTO cache (key, value, expires_at, created_at) VALUES (?, ?, ?, ?)")
                .let { preparedStatement ->
                    preparedStatement.setString(1, key)
                    preparedStatement.setObject(2, value)
                    preparedStatement.setLong(3, expiresAt)
                    preparedStatement.setLong(4, createdAt)
                    preparedStatement.executeUpdate()
                }
        }

        private fun deleteFromDatabase(key: String) {
            connection.prepareStatement("DELETE FROM cache WHERE key = ?").let { preparedStatement ->
                preparedStatement.setString(1, key)
                preparedStatement.executeUpdate()
            }
        }

        private fun clearDatabase() {
            connection.createStatement().let { statement ->
                statement.execute("DELETE FROM cache")
            }
        }

        private fun getCacheSize(): Int {
            connection.createStatement().let { statement ->
                val resultSet = statement.executeQuery("SELECT COUNT(*) FROM cache")
                if (resultSet.next()) {
                    return resultSet.getInt(1)
                }
            }
            return 0
        }

        private fun evictOldestItem() {
            connection.createStatement().let { statement ->
                // 获取最旧的缓存项
                val resultSet = statement.executeQuery("SELECT key FROM cache ORDER BY created_at ASC LIMIT 1")
                if (resultSet.next()) {
                    val oldestKey = resultSet.getString(1)
                    deleteFromDatabase(oldestKey)
                }
            }
        }

        private fun evictExpiredItems() {
            val currentTime = System.currentTimeMillis()
            connection.prepareStatement("DELETE FROM cache WHERE expires_at < ?").let { preparedStatement ->
                preparedStatement.setLong(1, currentTime)
                preparedStatement.executeUpdate()
            }
        }
    }
}