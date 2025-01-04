package io.github.zimoyin

import io.github.zimoyin.ra3.config.MyService
import org.junit.jupiter.api.Test
import org.mybatis.spring.annotation.MapperScan
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.EnableCaching
import org.springframework.stereotype.Service
import java.util.concurrent.CountDownLatch

@SpringBootTest
class ApplicationStartTests {


    @Test
    fun contextLoads() {
        // 确保应用程序上下文成功加载
    }
}