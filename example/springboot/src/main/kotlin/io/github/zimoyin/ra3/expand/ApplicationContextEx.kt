package io.github.zimoyin.ra3.expand

import org.springframework.aop.framework.AopProxyUtils
import org.springframework.aop.framework.AopProxyUtils.getSingletonTarget
import org.springframework.aop.support.AopUtils
import org.springframework.context.ApplicationContext

/**
 *
 * @author : zimo
 * @date : 2025/01/04
 */


fun <T> ApplicationContext.getBeanByName(name: String): T {
    return this.getBean(name) as T
}

fun ApplicationContext.getTargetBean(name: String): Any {
    val bean = getBean(name)
    return AopProxyUtils.getSingletonTarget(bean) ?: bean
//    return AopUtils.getTargetClass(bean)
}