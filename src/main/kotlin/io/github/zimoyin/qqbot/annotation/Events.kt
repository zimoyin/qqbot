package io.github.zimoyin.qqbot.annotation

import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.event.handler.DefaultAbsEventHandler
import io.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import kotlin.reflect.KClass

/**
 *
 * @author : zimo
 * @date : 2023/12/08
 * 事件注解
 */
annotation class EventAnnotation{
    /**
     * **EventMetaType 注解**
     *
     * 该注解用于拓展元事件，直接与从服务器接收的数据进行交互。通常用于处理框架中未声明或未实现的官方事件。
     *
     * 如果需要自定义事件，建议继承 [EventAnnotation] 类，并使用 [EventBus] 进行事件分发。请注意，在选择事件总线时要仔细考虑，通常使用 [github.zimoyin.event.EventBus]。
     *
     * **事件元类型注解**
     *
     * 该注解表示事件的元类型。元类型是指从服务器接收的事件元数据（JSON 格式），其中包含未经任何处理的原始事件名称。
     *
     * 使用示例：
     * ```kotlin
     * @EventMetaType("Test_Event_Type")
     * class CustomEvent : Event() {
     *     // 事件实现在这里
     * }
     * ```
     *
     * 之后监听该事件，该事件会自动注册进默认的事件总线。
     */
    @Target(AnnotationTarget.CLASS)
    annotation class EventMetaType(val metadataType: String)

    /**
     * **EventHandler 注解**
     *
     * 该注解用于拓展元事件的事件生成与处理器，直接与从服务器接收的数据进行交互。通常用于处理框架中未声明或未实现的官方事件。
     *
     *
     * **事件处理器注解**
     *
     * 该注解表示事件的处理器。用于处理从服务器接收的事件元数据（JSON 格式），将他变为被该注解锁标注的事件类型，并在默认的事件总线上进行分发。
     *
     * 使用示例：
     * ```kotlin
     * @EventHandler(Application::class)
     * class CustomEvent : Event() {
     *     // 事件实现在这里
     * }
     * ```
     * 之后监听该事件，该事件会自动注册进默认的事件总线。
     *
     * 注意如果注册的处理器的返回值类型与事件的类型不一致，则广播事件的时候无法广播到该事件，而是广播的处理器返回的事件
     *
     */
    @Target(AnnotationTarget.CLASS)
    annotation class EventHandler(
        val eventHandler: KClass<out AbsEventHandler<out Event>> = DefaultAbsEventHandler::class,
        val ignore: Boolean = false
    )
}
