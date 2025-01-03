package io.github.zimoyin.annotations

import io.github.zimoyin.qqbot.event.events.Event
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
annotation class EventHandle(
    val event: KClass<out Event> = Event::class,
    val executeMethod: String = "execute",
    val enabled: Boolean = true,
)
