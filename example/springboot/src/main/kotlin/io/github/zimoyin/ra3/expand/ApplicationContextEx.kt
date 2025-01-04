package io.github.zimoyin.ra3.expand

import org.springframework.context.ApplicationContext

/**
 *
 * @author : zimo
 * @date : 2025/01/04
 */


fun <T> ApplicationContext.getBeanByName(name:String): T {
   return this.getBean(name) as T
}