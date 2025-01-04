package io.github.zimoyin

import org.springframework.cache.Cache
import org.sqlite.SQLiteDataSource

/**
 *
 * @author : zimo
 * @date : 2025/01/04
 */
fun main() {
    val source = SQLiteDataSource()
    source.url = "jdbc:sqlite::memory:"
    source.databaseName = "memory"

    source.connection.use { connection ->
        connection.createStatement().use { statement ->
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS cache (
                        key TEXT PRIMARY KEY,
                        value BLOB,
                        expires_at INTEGER
                    )
                """)
        }
        println("Initialized SQLite cache")
    }

    source.connection.use { connection ->
        connection.prepareStatement("SELECT value FROM cache WHERE key = ?").use { preparedStatement ->
            preparedStatement.setString(1, 5.toString())
            preparedStatement.executeQuery().use { resultSet ->
                if (resultSet.next()) {
                    val value = resultSet.getObject(1)
                    println(value)
                }
            }
        }
        // 插入
        connection.prepareStatement("INSERT INTO cache (key, value, expires_at) VALUES (?, ?, ?)").apply {
            setString(1, "key")
            setString(2, "value")
            setLong(3, System.currentTimeMillis() + 1000 * 60 * 60 * 24)
            executeUpdate()
        }
        // 查询
        connection.prepareStatement("SELECT value FROM cache WHERE key = ?").let {
            it.setString(1, "key")
            it.executeQuery().use { resultSet ->
                if (resultSet.next()) {
                    val value = resultSet.getString("value")
                    println("Retrieved value from cache: $value")
                } else {
                    println("No value found in cache")
                }
            }
        }
    }
}