package io.github.zimoyin.qqbot.event.supporter

import io.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import io.github.zimoyin.qqbot.LocalLogger
import io.github.zimoyin.qqbot.SystemLogger
import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.Bot
import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.exception.EventBusException
import io.github.zimoyin.qqbot.utils.ex.promise
import io.github.zimoyin.qqbot.utils.vertx
import io.github.zimoyin.qqbot.utils.vertxWorker
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.Message
import io.vertx.core.eventbus.MessageConsumer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.function.Consumer

/**
 * 事件总线基础类
 */
open class BotEventBus(val vertx: Vertx) {
    val bus = vertx.eventBus()
    val logger by lazy { LocalLogger(BotEventBus::class.java) }
    val consumers = HashSet<MessageConsumer<*>>()
    var debugLogger: Boolean = true


    /**
     * 广播事件：只将当前事件广播到订阅当前事件的接收者。不对当前事件的父事件的接收者进行广播
     * 将当前的事件以泛型描述的类型进行广播
     */
    inline fun <reified T : Event> publish(message: T) {
        bus.publish(
            T::class.java.name,
            message,
            DeliveryOptions().addHeader("metadata", message.metadata).setLocalOnly(true)
        )
    }


    /**
     * 广播事件：只将当前事件广播到订阅当前事件的接收者。不对当前事件的父事件的接收者进行广播
     * 自动推导类型： 将当前的事件以这个事件的Class类型为基础进行广播
     */
    fun publishAuto(message: Event) {
        bus.publish(message::class.java.name, message)
    }

    /**
     * 广播事件：只将当前事件广播到订阅当前事件的接收者。不对当前事件的父事件的接收者进行广播
     * 将当前的事件以Class描述的类型进行广播
     */
    fun publish(cls: Class<out Event>, message: Event) {
        bus.publish(cls.name, message)
    }

    /**
     * 发布消息。
     * 该消息将传递给注册到该地址的所有处理程序。
     *
     * @param address–发布到的地址
     * @param message–消息，可能为null
     */
    fun publishAddress(address: String, message: Any?) {
        bus.publish(address, message)
    }

    /**
     * 广播事件，能将当前事件（当前事件的Class为基础）广播到当前事件与父事件的接收者。
     * 将当前的事件以泛型描述的类型进行广播。
     * 该方法只会截至到当前事件的继承树中的一级接口与全部类
     */
    @OptIn(UntestedApi::class)
    inline fun <reified T : Event> broadcast(message: T) {
        val currentClass: Class<*> = T::class.java
        broadcastMessage(currentClass, message, false)
    }

    /**
     * 广播事件，能将当前事件广播（当前事件的Class为基础）到当前事件与父事件的接收者。
     * 将当前的事件以Class描述的类型进行广播
     * 该方法只会截至到当前事件的继承树中的一级接口与全部类
     */
    @OptIn(UntestedApi::class)
    fun broadcast(cls: Class<out Event>, message: Event) {
        broadcastMessage(cls, message, false)
    }

    /**
     * 广播事件，能将当前事件广播到当前事件与父事件的接收者。
     * 自动推导类型： 将当前的事件以这个事件的Class类型为基础进行广播
     * 该方法只会传播事件中的继承树中所有来自Event的接口与类
     */
    @OptIn(UntestedApi::class)
    fun broadcastAuto(message: Event) {
        val currentClass: Class<*> = message::class.java
        broadcastMessage(currentClass, message)
    }

    @UntestedApi
    @JvmName("banOnUseBroadcastMessage")
    fun broadcastMessage(
        clz: Class<*>,
        message: Event,
        deepPropagation: Boolean = true
    ) {
        if (EventMapping.get(message.metadataType)?.eventCls?.name == null) {
            logger.warn("正在广播一个未在EventMapping中注册的事件 [${clz.name}]")
        } else if (EventMapping.get(message.metadataType)?.eventCls?.name != clz.name) {
            logger.warn("服务器要求下发事件为[${EventMapping.get(message.metadataType)?.eventCls?.name}], 但是实际下发事件为[${clz.name}];")
            logger.warn("请实现该事件的处理器，不要使用父级处理器。否则不会广播该事件，而是广播当前事件并将当前事件从该处理器返回的事件层级中开始广播")
        }

        val root = Event::class.java
        var currentClass = clz
        while (root.isAssignableFrom(currentClass) || root == currentClass) {
            if (debugLogger) logger.debug(
                "发布事件[${message.metadataType}:${message.botInfo.token.appID}]: ${currentClass.name}"
            )
            bus.publish(currentClass.name, message)
            broadcastInterface(currentClass.interfaces, root, message, arrayListOf(), deepPropagation)
            currentClass = currentClass.superclass
        }
    }

    private fun broadcastInterface(
        its: Array<Class<*>>,
        root: Class<*>,
        message: Event,
        blackList: ArrayList<Class<*>> = arrayListOf(),
        deepPropagation: Boolean = true
    ) {
        its.forEach {
            if (blackList.contains(it)) return
            if (root.isAssignableFrom(it) || root == it) {
                bus.publish(it.name, message)
                if (debugLogger) logger.debug(
                    "发布事件[${message.metadataType}:${message.botInfo.token.appID}]: ${it.name}",
                )
                blackList.add(it)
            } else {
                return@forEach
            }

            if (deepPropagation) broadcastInterface(it.interfaces, root, message, blackList)
        }
    }


    /**
     * 全局事件监听，如果配置了集群则可以监听来自于所有的 vertx 的事件
     */
    @JvmOverloads
    fun <T : Event> onEvent(
        cls: Class<out T>,
        isUseWorkerThread: Boolean = false,
        vertx: Vertx = this.vertx,
        callback: Consumer<T>
    ): Int {
        /**
         * 通过 Event 上的注解可以确定事件的元类型  然后就去映射他们
         * 优点：方便框架使用者实现官方的事件类型
         * 缺点：繁琐的配置，无法解析没有监听的事件
         *
         * 硬编码方式：
         * 优点：一波梭
         * 缺点：无法直接让框架使用者实现官方的事件类型，需要监听Event 事件后再去广播
         */
        EventMapping.add(cls)
        val stackTrace = Thread.currentThread().stackTrace.getOrNull(1)?.toString() ?: ""

        val consumer = vertx.eventBus().consumer<T>(cls.name) { msg ->
            val scope = if (isUseWorkerThread) Dispatchers.vertxWorker(vertx) else Dispatchers.vertx(vertx)
            CoroutineScope(scope).launch {
                kotlin.runCatching {
                    callback.accept(msg.body())
                }.onFailure {
                    throw EventBusException(RuntimeException("Caller: $stackTrace", it))
                }
            }
        }

        consumers.add(consumer)
        return consumer.hashCode()
    }

    /**
     * 全局事件监听，如果配置了集群则可以监听来自于所有的 vertx 的事件
     * 如果只想监听某一杠Vertx 的事件，通过bot的 config 来监听
     *
     */
    @JvmOverloads
    inline fun <reified T : Event> onEvent(
        isUseWorkerThread: Boolean = false,
        vertx: Vertx = this.vertx,
        crossinline callback: suspend Message<T>.(message: T) -> Unit
    ): Int {
        /**
         * 通过 Event 上的注解可以确定事件的元类型  然后就去映射他们
         * 优点：方便框架使用者实现官方的事件类型
         * 缺点：繁琐的配置，无法解析没有监听的事件
         *
         * 硬编码方式：
         * 优点：一波梭
         * 缺点：无法直接让框架使用者实现官方的事件类型，需要监听Event 事件后再去广播
         */
        EventMapping.add(T::class.java)
        val stackTrace = Thread.currentThread().stackTrace.getOrNull(1)?.toString() ?: ""

        val consumer = vertx.eventBus().consumer(T::class.java.name) { msg ->
            val scope = if (isUseWorkerThread) Dispatchers.vertxWorker(vertx) else Dispatchers.vertx(vertx)
            CoroutineScope(scope).launch {
                kotlin.runCatching {
                    msg.callback(msg.body())
                }.onFailure {
                    throw EventBusException(RuntimeException("Caller: $stackTrace", it))
                }
            }
        }

        consumers.add(consumer)
        return consumer.hashCode()
    }

    /**
     * 全局事件监听，如果配置了集群则可以监听来自于所有的 vertx 的事件
     */
    @JvmOverloads
    inline fun <reified T : Event> onVertxEvent(
        isUseWorkerThread: Boolean = false,
        vertx: Vertx = this.vertx,
        crossinline callback: suspend Message<T>.(message: T) -> Unit
    ): Int {
        /**
         * 通过 Event 上的注解可以确定事件的元类型  然后就去映射他们
         * 优点：方便框架使用者实现官方的事件类型
         * 缺点：繁琐的配置，无法解析没有监听的事件
         *
         * 硬编码方式：
         * 优点：一波梭
         * 缺点：无法直接让框架使用者实现官方的事件类型，需要监听Event 事件后再去广播
         */
        EventMapping.add(T::class.java)
        val stackTrace = Thread.currentThread().stackTrace.getOrNull(1)?.toString() ?: ""

        val consumer = vertx.eventBus().localConsumer(T::class.java.name) { msg ->
            val scope = if (isUseWorkerThread) Dispatchers.vertxWorker(vertx) else Dispatchers.vertx(vertx)
            CoroutineScope(scope).launch {
                kotlin.runCatching {
                    msg.callback(msg.body())
                }.onFailure {
                    throw EventBusException(RuntimeException("Caller: $stackTrace", it))
                }
            }
        }

        consumers.add(consumer)
        return consumer.hashCode()
    }

    /**
     * 全局事件监听，如果配置了集群则可以监听来自于所有的 vertx 的事件,并监听某一个Bot 的事件
     */
    @JvmOverloads
    fun <T : Event> onBotEvent(
        bot: Bot, cls: Class<out T>,
        isUseWorkerThread: Boolean = false,
        vertx: Vertx = this.vertx,
        callback: Consumer<T>
    ): Int {
        /**
         * 通过 Event 上的注解可以确定事件的元类型  然后就去映射他们
         * 优点：方便框架使用者实现官方的事件类型
         * 缺点：繁琐的配置，无法解析没有监听的事件
         *
         * 硬编码方式：
         * 优点：一波梭
         * 缺点：无法直接让框架使用者实现官方的事件类型，需要监听Event 事件后再去广播
         */
        EventMapping.add(cls)
        val stackTrace = Thread.currentThread().stackTrace.getOrNull(1)?.toString() ?: ""

        val consumer = vertx.eventBus().consumer<T>(cls.name) { msg ->
            val scope = if (isUseWorkerThread) Dispatchers.vertxWorker(vertx) else Dispatchers.vertx(vertx)
            CoroutineScope(scope).launch {
                kotlin.runCatching {
                    if ((msg.body() as T).botInfo.token.appID == bot.config.token.appID) callback.accept(msg.body())
                }.onFailure {
                    throw EventBusException(RuntimeException("Caller: $stackTrace", it))
                }
            }
        }

        consumers.add(consumer)
        return consumer.hashCode()
    }
    /**
     * 全局事件监听，如果配置了集群则可以监听来自于所有的 vertx 的事件,并监听某一个Bot 的事件
     * 监听某一个Vertx 的事件
     *
     */
    @JvmOverloads
    fun <T : Event> onVertxBotEvent(
        bot: Bot, cls: Class<out T>,
        isUseWorkerThread: Boolean = false,
        vertx: Vertx = this.vertx,
        callback: Consumer<T>
    ): Int {
        /**
         * 通过 Event 上的注解可以确定事件的元类型  然后就去映射他们
         * 优点：方便框架使用者实现官方的事件类型
         * 缺点：繁琐的配置，无法解析没有监听的事件
         *
         * 硬编码方式：
         * 优点：一波梭
         * 缺点：无法直接让框架使用者实现官方的事件类型，需要监听Event 事件后再去广播
         */
        EventMapping.add(cls)
        val stackTrace = Thread.currentThread().stackTrace.getOrNull(1)?.toString() ?: ""

        val consumer = vertx.eventBus().localConsumer<T>(cls.name) { msg ->
            val scope = if (isUseWorkerThread) Dispatchers.vertxWorker(vertx) else Dispatchers.vertx(vertx)
            CoroutineScope(scope).launch {
                kotlin.runCatching {
                    if ((msg.body() as T).botInfo.token.appID == bot.config.token.appID) callback.accept(msg.body())
                }.onFailure {
                    throw EventBusException(RuntimeException("Caller: $stackTrace", it))
                }
            }
        }

        consumers.add(consumer)
        return consumer.hashCode()
    }

    /**
     * 全局事件监听，如果配置了集群则可以监听来自于所有的 vertx 的事件,并监听某一个Bot 的事件
     */
    @JvmOverloads
    inline fun <reified T : Event> onBotEvent(
        bot: Bot,
        isUseWorkerThread: Boolean = false,
        vertx: Vertx = this.vertx,
        crossinline callback: suspend Message<T>.(message: T) -> Unit
    ): Int {
        EventMapping.add(T::class.java)
        val stackTrace = Thread.currentThread().stackTrace.getOrNull(1)?.toString() ?: ""
        val consumer = vertx.eventBus().consumer(T::class.java.name) { msg ->
            val scope = if (isUseWorkerThread) Dispatchers.vertxWorker(vertx) else Dispatchers.vertx(vertx)
            CoroutineScope(scope).launch {
                kotlin.runCatching {
                    if ((msg.body() as T).botInfo.token.appID == bot.config.token.appID) msg.callback(msg.body())
                }.onFailure {
                    throw EventBusException(RuntimeException("Caller: $stackTrace", it))
                }
            }
        }
        consumers.add(consumer)
        return consumer.hashCode()
    }

    /**
     * 全局事件监听，如果配置了集群则可以监听来自于所有的 vertx 的事件,并监听某一个Bot 的事件
     */
    @JvmOverloads
    fun <T : Event> onBotEvent(
        appID: String,
        cls: Class<out T>,
        isUseWorkerThread: Boolean = false,
        vertx: Vertx = this.vertx,
        callback: Consumer<T>,
    ): Int {
        EventMapping.add(cls)
        val stackTrace = Thread.currentThread().stackTrace.getOrNull(1)?.toString() ?: ""
        val consumer = vertx.eventBus().consumer<T>(cls.name) { msg ->
            val scope = if (isUseWorkerThread) Dispatchers.vertxWorker(vertx) else Dispatchers.vertx(vertx)
            CoroutineScope(scope).launch {
                kotlin.runCatching {
                    if ((msg.body() as T).botInfo.token.appID == appID) callback.accept(msg.body())
                }.onFailure {
                    throw EventBusException(RuntimeException("Caller: $stackTrace", it))
                }
            }
        }
        consumers.add(consumer)
        return consumer.hashCode()
    }

    /**
     * 全局事件监听，如果配置了集群则可以监听来自于所有的 vertx 的事件,并监听某一个Bot 的事件
     */
    @JvmOverloads
    inline fun <reified T : Event> onBotEvent(
        appID: String,
        isUseWorkerThread: Boolean = false,
        vertx: Vertx = this.vertx,
        crossinline callback: suspend Message<T>.(message: T) -> Unit,
    ): Int {
        EventMapping.add(T::class.java)
        val stackTrace = Thread.currentThread().stackTrace.getOrNull(1)?.toString() ?: ""
        val consumer = vertx.eventBus().consumer(T::class.java.name) { msg ->
            val scope = if (isUseWorkerThread) Dispatchers.vertxWorker(vertx) else Dispatchers.vertx(vertx)
            CoroutineScope(scope).launch {
                kotlin.runCatching {
                    if ((msg.body() as T).botInfo.token.appID == appID) msg.callback(msg.body())
                }.onFailure {
                    throw EventBusException(RuntimeException("Caller: $stackTrace", it))
                }
            }
        }
        consumers.add(consumer)
        return consumer.hashCode()
    }

    /**
     * 取消监听
     */
    fun unregister(id: Int): Future<Void> {
        val promise = promise<Void>()
        consumers.find { it.hashCode() == id }.let {mc->
            if (mc == null) {
                promise.fail(NullPointerException("Not Found Consumer"))
                return@let
            }
            mc.unregister().onComplete {
                promise.complete()
                consumers.remove(mc)
            }.onFailure { promise.fail(it) }
        }
        return promise.future()
    }

    /**
     * 获取一个监听
     */
    fun getConsumer(id: Int): MessageConsumer<*>? {
        return consumers.find { it.hashCode() == id }
    }

    fun clear() {
        consumers.forEach { it.unregister() }
        consumers.clear()
    }
}

/**
 *
 * @author : zimo
 * @project : qqbot_gf
 * @date : 2023/12/06/17:29
 * @description ：默认的全局事件总线,可用监听同一个vertx中传输的所有机器人的事件。也可用在该总线上发布事件
 */
object GlobalEventBus : BotEventBus(GLOBAL_VERTX_INSTANCE) {
    init {
        SystemLogger.debug("Global EventBus 初始化完成")
    }
}
