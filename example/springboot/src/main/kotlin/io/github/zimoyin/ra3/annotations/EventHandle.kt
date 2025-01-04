package io.github.zimoyin.ra3.annotations

import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.event.events.message.MessageEvent
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
@Component
annotation class EventHandle(
    val event: KClass<out Event> = Event::class,
    val executeMethod: String = "execute",
    val enabled: Boolean = true,
)
