package io.github.zimoyin.ra3.aspect

import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import io.github.zimoyin.ra3.service.PlayerCarService
import io.github.zimoyin.ra3.service.RegisterService
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RegisterAOP(val value: String = "")

@Aspect
@Component //交给spring进行管理
@Order(5)
class ImplRegisterAop(
    val registerService: RegisterService,
    val service: PlayerCarService
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    @Pointcut(" execution(* io.github.zimoyin.ra3.service..*.*(..))")
    fun testPoint() {}

    @Around("@annotation(io.github.zimoyin.ra3.aspect.RegisterAOP)") //@annotation(test)获取注解对象
    fun register(joinPoint: ProceedingJoinPoint):Any? {
        println("执行了切面")
        val event = kotlin.runCatching {
            joinPoint.args.filterIsInstance<MessageEvent>().firstOrNull()
        }.getOrNull()

        if (event == null) {
            logger.warn("${joinPoint.signature} 上不存在 MessageEvent 参数")
        } else {
            if (!registerService.isRegistered(event.sender.id)) {
                event.reply("您还未登记在册！ 请使用 /注册 命令注册")
                return null
            }
        }
        return joinPoint.proceed()
    }

    @RegisterAOP
    fun test(){

    }
}