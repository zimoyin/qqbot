package io.github.zimoyin.ra3.framework

import io.github.zimoyin.ra3.ApplicationStart
import io.github.zimoyin.ra3.annotations.EventHandle
import io.github.zimoyin.ra3.annotations.IEvent
import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.sql.Connection


/**
 *
 * @author : zimo
 * @date : 2025/01/03
 */
@Component("EventProcessor")
class EventProcessor(val applicationContext: ApplicationContext) {

    private val eventMethods = HashMap<String, EventMethod>()
    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var start: ApplicationStart

    @EventListener(ApplicationReadyEvent::class)
    fun init() {
        kotlin.runCatching {
            for (bean in initializedBeanInstances) {
                kotlin.runCatching {
                    register(bean)
                }.onFailure {
                    logger.error("bean: ${bean.javaClass} 注册事件失败", it)
                }
            }
        }.onFailure {
            logger.error("初始化事件处理器失败", it)
        }.onSuccess {
            logger.info("初始化事件处理器完成")
        }
        for ((key, method) in eventMethods) {
            start.bot.onEvent(method.event) {
                kotlin.runCatching {
                    method.invoke(it)
                }.onFailure {
                    logger.error("$key 执行事件时失败", it)
                }
            }
        }
    }

    /**
     * 初始化完成后的 bean 列表
     */
    private val initializedBeanInstances by lazy {
        applicationContext.beanDefinitionNames.filter {
            it != this.javaClass.simpleName &&
                    it != CommanderProcessor::class.simpleName &&
                    it != RouterProcessor::class.simpleName &&
                    it != Connection::class.simpleName
        }.mapNotNull {
            applicationContext.getBean(it)
        }
    }

    val eventBeans by lazy {
        initializedBeanInstances.filter(this::isEventer)
    }

    private fun register(bean: Any) {
        val beanClass = bean.javaClass
        val annotationClass = EventHandle::class.java

        // 注册方法级别的事件
        for (method in beanClass.methods) {
            val annotation = method.getAnnotation(annotationClass) ?: continue
            kotlin.runCatching {
                val first = method.parameterTypes.firstOrNull()
                    ?: throw NullPointerException("${bean.javaClass}: The ${annotation.executeMethod} method parameter is not one")
                // first 必须是 Event 类型或者他的子类
                if (!Event::class.java.isAssignableFrom(first))
                    throw IllegalArgumentException("${bean.javaClass}: The parameter type of ${annotation.executeMethod} method must be either the type of Event or its subclass") //
                eventMethods[beanClass.packageName + "." + method.name] = EventMethod(
                    event = annotation.event.java,
                    executeMethod = method,
                    enabled = annotation.enabled,
                    bean = bean,
                )
            }.onFailure {
                logger.error("bean: ${bean.javaClass} 注册方法级别事件时失败", it)
            }
        }


        // 注册类级别的事件
        kotlin.runCatching {
            beanClass.getAnnotation(annotationClass)?.let { annotation ->
                val method = beanClass.methods.firstOrNull() {
                    it.name == annotation.executeMethod && it.parameterCount == 1
                }
                    ?: throw NullPointerException("${bean.javaClass}: The ${annotation.executeMethod} method of the annotation command does not exist or the method parameter is not one")

                val first = method.parameterTypes.first()
                // first 必须是 MessageEvent 类型或者他的子类
                if (!Event::class.java.isAssignableFrom(first)) throw IllegalArgumentException("${bean.javaClass}: The parameter type of ${annotation.executeMethod} method must be either the type of Event or its subclass") //
                eventMethods[beanClass.packageName + "." + method.name] = EventMethod(
                    event = annotation.event.java,
                    executeMethod = method,
                    enabled = annotation.enabled,
                    bean = bean,
                )
            }
        }.onFailure {
            logger.error("bean: ${bean.javaClass} 注册注解类事件时失败", it)
        }
        kotlin.runCatching {
            val isIEvent = IEvent::class.java.isAssignableFrom(beanClass)
            if (!isIEvent) return
            val enabled = beanClass.getMethod("enabled").invoke(bean) as Boolean
            val method = beanClass.methods.first {
                it.name == "execute"
            }
            val name = beanClass.packageName + "." + method.name
            eventMethods[name] = EventMethod(
                event = method.parameterTypes.first() as Class<out MessageEvent>,
                executeMethod = method,
                enabled = enabled,
                bean = bean,
            )
        }.onFailure {
            logger.error("bean: ${bean.javaClass} 注册实现类事件时失败", it)
        }
    }

    fun isEventer(bean: Any): Boolean {
        val annotation = EventHandle::class.java
        bean.javaClass.isAnnotationPresent(annotation).let {
            if (it) return true
        }
        val clazz = bean.javaClass
        IEvent::class.java.isAssignableFrom(clazz).let {
            if (it) return true
        }

        for (method in clazz.methods) {
            method.isAnnotationPresent(annotation).let {
                if (it) return true
            }
        }
        return false
    }

    class EventMethod(
        val event: Class<out Event>,
        val executeMethod: Method,
        val enabled: Boolean,
        val bean: Any,
    ) {
        init {
            executeMethod.isAccessible = true
        }

        fun invoke(event: Event) {
            if (enabled) executeMethod.invoke(bean, event)
        }
    }
}