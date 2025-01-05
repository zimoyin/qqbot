package io.github.zimoyin.ra3.annotations

import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

/**
 *
 * @author : zimo
 * @date : 2025/01/03
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Aspect
@Component
annotation class Commander(
    val name: String,
    val event: KClass<out MessageEvent> = MessageEvent::class,
    val executeMethod: String = "execute",
    val enabled: Boolean = true,
)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Component
annotation class NotFundCommand()
