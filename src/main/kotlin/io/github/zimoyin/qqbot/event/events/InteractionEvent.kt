package io.github.zimoyin.qqbot.event.events

import io.github.zimoyin.qqbot.LocalLogger
import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Contact
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import io.github.zimoyin.qqbot.bot.message.type.MessageItem
import io.github.zimoyin.qqbot.event.handler.message.InteractionEventHandler
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean
import io.github.zimoyin.qqbot.net.bean.message.InteractionEventData
import io.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.github.zimoyin.qqbot.net.http.api.replyInteractions
import io.github.zimoyin.qqbot.utils.ex.promise
import io.github.zimoyin.qqbot.utils.io
import io.vertx.core.Future
import kotlinx.coroutines.delay

/**
 * @author: zimo
 * @date:   2024/12/27
 *
 * 互动事件
 */
@EventAnnotation.EventMetaType("INTERACTION_CREATE")
@EventAnnotation.EventHandler(InteractionEventHandler::class)
class InteractionEvent(
    override val metadataType: String = "MESSAGE_CREATE",
    override val metadata: String,
    override val botInfo: BotInfo,
    override val eventID: String,
    val windows: Contact,
    val data: InteractionEventData,
) : Event {
    private var isCallOk = false

    init {
        io {
            delay(1500)
            if (!isCallOk) {
                LocalLogger(InteractionEvent::class.java).error("InteractionEvent 未调用 ok() 方法，已自动调用")
                ok()
            }
        }
    }

    /**
     * 确认消息客户端已经收到信息
     * 0 成功
     * 1 操作失败
     * 2 操作频繁
     * 3 重复操作
     * 4 没有权限
     * 5 仅管理员操作
     */
    fun ok(code:Int = 0): Future<Void> {
        isCallOk = true
        return HttpAPIClient.replyInteractions(botInfo.token, data.id,code)
    }

    fun reply(msg: String): Future<SendMessageResultBean> {
        return reply(MessageChainBuilder().append(msg).build())
    }

    fun reply(message: MessageChain): Future<SendMessageResultBean> {
        val eventID = if (message.replyEventID.isNullOrEmpty()) {
            eventID
        } else {
            message.replyEventID
        }
        if (eventID.isEmpty()) return promise<SendMessageResultBean>().apply {
            tryFail("eventID is null")
        }.future()
        return windows.send(
            MessageChainBuilder().appendEventId(eventID).append(message).build()
        )
    }

    fun reply(vararg items: MessageItem): Future<SendMessageResultBean> {
        return reply(MessageChainBuilder().appendEventId(eventID).appendItems(*items).build())
    }
}
