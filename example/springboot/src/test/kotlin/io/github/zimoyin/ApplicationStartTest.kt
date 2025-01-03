package io.github.zimoyin

import io.github.zimoyin.annotations.ICommand
import io.github.zimoyin.annotations.Commander
import io.github.zimoyin.commander.HelloCommand
import org.junit.jupiter.api.Test

import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ApplicationStartTests {

    @Test
    fun contextLoads() {

    }

}

fun main() {
    val java = io.github.zimoyin.Test()
    println(isCommander(java))
}

fun isCommander(bean: Any): Boolean {
    val commander = bean.javaClass.getAnnotation(Commander::class.java)?.let {
        return true
    }

    // bean 是 Command(接口) 的实现类。或者 bean 是 实现了 Command 的类子孙类
    return ICommand::class.java.isAssignableFrom(bean.javaClass)
}

class Test :HelloCommand()