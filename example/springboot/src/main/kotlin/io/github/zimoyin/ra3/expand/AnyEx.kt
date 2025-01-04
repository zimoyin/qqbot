package io.github.zimoyin.ra3.expand

import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ConfigurableApplicationContext


/**
 *
 * @author : zimo
 * @date : 2025/01/04
 */
fun Any.registerSingletonBean(applicationContext: ApplicationContext, beanName: String = this::class.java.simpleName) {
    if (applicationContext is ConfigurableApplicationContext) {
        val beanFactory = applicationContext.beanFactory as DefaultListableBeanFactory
        // 注册一个单例bean，使用RootBeanDefinition来定义bean
        beanFactory.registerSingleton(beanName, this)
    }
}