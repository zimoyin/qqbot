package io.github.zimoyin.ra3.annotations

import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import io.vertx.core.http.HttpMethod
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
annotation class RouterController


/**
 *
 * @author : zimo
 * @date : 2025/01/03
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Router(
    val path: String,
    val method: String = ""
)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Rout(
    val path: String,
    val method: String = ""
)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class RouterGet(
    val path: String,
    val method: String = "GET"
)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class RouterPost(
    val path: String,
    val method: String = "POST"
)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class RouterPut(
    val path: String,
    val method: String = "PUT"
)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class RouterDelete(
    val path: String,
    val method: String = "DELETE"
)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class RouterPatch(
    val path: String,
    val method: String = "PATCH"
)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class RouterHead(
    val path: String,
    val method: String = "HEAD"
)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class RouterOptions(
    val path: String,
    val method: String = "OPTIONS"
)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AutoClose

