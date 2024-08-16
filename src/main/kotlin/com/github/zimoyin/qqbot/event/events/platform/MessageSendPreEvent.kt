package com.github.zimoyin.qqbot.event.events.platform

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.contact.Contact
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.event.handler.NoneEventHandler
import org.slf4j.LoggerFactory

/**
 *
 * @author : zimo
 * @date : 2023/12/19
 *
 * 信息向服务器推送前触发该事件
 * 该事件为全局事件，只从全局事件总线中传递
 * 注意该事件只是作为一个通知，不要使用该事件拦截与修改待发送的信息
 *
 * 如果相对发送的信息进行审核使用 MessageSendPreEvent.interceptor 方法进行拦截，方法返回 MessageSendPreEvent 对象，该对象将作为发送的信息元，你有该方法传入的 MessageSendPreEvent 进行修改指定的数据
 *
 * ```
 * MessageSendPreEvent.interceptor {
 *    return@interceptor it.apply {
 *          intercept = true
 *     }
 * }
 * ```
 */
@EventAnnotation.EventMetaType("Platform_MessageSendPreEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
open class MessageSendPreEvent(
    override val metadata: String = "Platform_MessageSendPreEvent",
    override val metadataType: String = "Platform_MessageSendPreEvent",
    open val msgID: String,
    open var messageChain: MessageChain,
    open val contact: Contact,
    override val botInfo: BotInfo = contact.botInfo,
    override val eventID: String ="",
    /**
     * 是否拦截
     */
    var intercept: Boolean = false,
) : PlatformEvent {
    companion object {
        private val Interceptors = ArrayList<(event: MessageSendPreEvent) -> MessageSendPreEvent>()

        fun interceptor(call: (event: MessageSendPreEvent) -> MessageSendPreEvent) {
            Interceptors.add(call)
        }

        fun result(event: MessageSendPreEvent): MessageSendPreEvent {
            var intercept = false
            var event2: MessageSendPreEvent? = null
            Interceptors.forEach {
                kotlin.runCatching {
                    val preEvent = it(event)
                    if (!preEvent.intercept) event2 = preEvent
                    intercept = intercept || preEvent.intercept
                }.onFailure {
                    LoggerFactory.getLogger(MessageSendPreEvent::class.java)
                        .error("MessageSendPreEvent interceptor error", it)
                }
            }
            event2?.intercept = intercept
            return event
        }
    }
}
