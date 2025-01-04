package io.github.zimoyin.ra3.framework

import io.github.zimoyin.ra3.ApplicationStart
import io.github.zimoyin.ra3.annotations.Commander
import io.github.zimoyin.ra3.annotations.ICommand
import io.github.zimoyin.ra3.annotations.NotFundCommand
import io.github.zimoyin.qqbot.bot.onEvent
import io.github.zimoyin.qqbot.command.SimpleCommandRegistrationCenter
import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import io.github.zimoyin.qqbot.exception.CommandNotFoundException
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import java.lang.reflect.Method


/**
 *
 * @author : zimo
 * @date : 2025/01/03
 */
@Component("CommanderProcessor")
class CommanderProcessor(val applicationContext: ApplicationContext) {

    private val commandMethods = HashMap<String, CommandMethod>()
    private val exceptionMethods = ArrayList<CommandMethod>()
    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var start: ApplicationStart

    @PostConstruct
    fun init() {
        kotlin.runCatching {
            for (bean in initializedBeanInstances) {
                kotlin.runCatching {
                    registerCommand(bean)
                }.onFailure {
                    logger.error("bean: ${bean.javaClass} 注册命令失败", it)
                }
            }
        }.onFailure {
            logger.error("初始化命令处理器失败", it)
        }.onSuccess {
            logger.info("初始化命令处理器完成")
        }
        for ((key, method) in commandMethods) {
            SimpleCommandRegistrationCenter.register(method.name) {
                kotlin.runCatching {
                    val event =
                        it?.event ?: throw IllegalArgumentException("$key ：Failed to execute command, event is empty")
                    method.invoke(event)
                }.onFailure {
                    logger.error("$key 执行命令时失败", it)
                }
            }
        }

        start.bot.onEvent<MessageEvent> {
            kotlin.runCatching {
                SimpleCommandRegistrationCenter.execute(it)
            }.onFailure { e ->
                if (e is CommandNotFoundException) {
                    logger.debug("执行命令失败: ${e.message}")
                    exceptionMethods.forEach { mm ->
                        kotlin.runCatching {
                            val method = mm.executeMethod
                            method.invoke(mm.bean, it)
                            method.isAccessible = true
                        }.onFailure {
                            it
                            logger.error("执行命令错误处理器时失败", it)
                        }
                    }
                } else logger.error("$e 执行命令时失败", e)
            }
        }
    }

    /**
     * 初始化完成后的 bean 列表
     */
    private val initializedBeanInstances by lazy {
        applicationContext.beanDefinitionNames.filter {
            it != this.javaClass.simpleName && it != EventProcessor::class.simpleName
        }.map {
            applicationContext.getBean(it)
        }
    }

    val commandBeans by lazy {
        initializedBeanInstances.filter(this::isCommander)
    }

    private fun registerCommand(bean: Any) {
        val beanClass = bean.javaClass
        val annotationClass = Commander::class.java
        val annotationClass2 = NotFundCommand::class.java

        // 注册方法级别的命令
        for (method in beanClass.methods) {
            val annotation = method.getAnnotation(annotationClass)
            if (annotation != null) kotlin.runCatching {
                val first = method.parameterTypes.firstOrNull()
                    ?: throw NullPointerException("${bean.javaClass}: The ${annotation.executeMethod} method parameter is not one")
                // first 必须是 MessageEvent 类型或者他的子类
                if (!MessageEvent::class.java.isAssignableFrom(first))
                    throw IllegalArgumentException("${bean.javaClass}: The parameter type of ${annotation.executeMethod} method must be either the type of Message Event or its subclass") //
                commandMethods[annotation.name] = CommandMethod(
                    name = annotation.name,
                    event = annotation.event.java,
                    executeMethod = method,
                    enabled = annotation.enabled,
                    bean = bean,
                )
            }.onFailure {
                logger.error("bean: ${bean.javaClass} 注册方法级别命令时失败", it)
            }
            method.getAnnotation(annotationClass2) ?: continue
            exceptionMethods.add(
                CommandMethod(
                    name = "",
                    event = MessageEvent::class.java,
                    executeMethod = method,
                    enabled = true,
                    bean = bean
                )
            )
        }


        // 注册类级别的命令
        kotlin.runCatching {
            beanClass.getAnnotation(annotationClass)?.let { annotation ->
                val method = beanClass.methods.firstOrNull() {
                    it.name == annotation.executeMethod && it.parameterCount == 1
                }
                    ?: throw NullPointerException("${bean.javaClass}: The ${annotation.executeMethod} method of the annotation command does not exist or the method parameter is not one")

                val first = method.parameterTypes.first()
                // first 必须是 MessageEvent 类型或者他的子类
                if (!MessageEvent::class.java.isAssignableFrom(first)) throw IllegalArgumentException("${bean.javaClass}: The parameter type of ${annotation.executeMethod} method must be either the type of Message Event or its subclass") //
                commandMethods[annotation.name] = CommandMethod(
                    name = annotation.name,
                    event = annotation.event.java,
                    executeMethod = method,
                    enabled = annotation.enabled,
                    bean = bean,
                )
            }
        }.onFailure {
            logger.error("bean: ${bean.javaClass} 注册注解类命令时失败", it)
        }
        kotlin.runCatching {
            val isCommand = ICommand::class.java.isAssignableFrom(beanClass)
            if (!isCommand) return
            val name = beanClass.getMethod("name").invoke(bean).toString()
            val enabled = beanClass.getMethod("enabled").invoke(bean) as Boolean
            val method = beanClass.methods.first {
                it.name == "execute"
            }
            commandMethods[name] = CommandMethod(
                name = name,
                event = method.parameterTypes.first() as Class<out MessageEvent>,
                executeMethod = method,
                enabled = enabled,
                bean = bean,
            )
        }.onFailure {
            logger.error("bean: ${bean.javaClass} 注册实现类命令时失败", it)
        }
    }

    fun isCommander(bean: Any): Boolean {
        val annotation = Commander::class.java
        bean.javaClass.isAnnotationPresent(annotation).let {
            if (it) return true
        }
        val clazz = bean.javaClass
        ICommand::class.java.isAssignableFrom(clazz).let {
            if (it) return true
        }

        for (method in clazz.methods) {
            method.isAnnotationPresent(annotation).let {
                if (it) return true
            }
        }
        return false
    }

    class CommandMethod(
        val name: String,
        val event: Class<out MessageEvent>,
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