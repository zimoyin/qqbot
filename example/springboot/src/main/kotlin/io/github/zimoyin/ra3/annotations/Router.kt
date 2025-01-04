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
annotation class Router(
    val path: String,
    val method: String = ""
)

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Component
annotation class Rout(
    val path: String,
    val method: String = ""
)

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Component
annotation class RouterGet(
    val path: String,
    val method: String = "GET"
)

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Component
annotation class RouterPost(
    val path: String,
    val method: String = "POST"
)

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Component
annotation class RouterPut(
    val path: String,
    val method: String = "PUT"
)

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Component
annotation class RouterDelete(
    val path: String,
    val method: String = "DELETE"
)

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Component
annotation class RouterPatch(
    val path: String,
    val method: String = "PATCH"
)

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Component
annotation class RouterHead(
    val path: String,
    val method: String = "HEAD"
)

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Component
annotation class RouterOptions(
    val path: String,
    val method: String = "OPTIONS"
)

