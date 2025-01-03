package io.github.zimoyin.annotations

import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import kotlin.reflect.KClass

/**
 *
 * @author : zimo
 * @date : 2025/01/03
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Commander(
    val name: String,
    val event: KClass<out MessageEvent> = MessageEvent::class,
    val executeMethod: String = "execute",
    val enabled: Boolean = true,
)

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class NotFundCommand()
