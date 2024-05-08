package com.github.zimoyin.qqbot.net.http.api.group

import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.contact.Group
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.event.events.MessageStartAuditEvent
import com.github.zimoyin.qqbot.event.events.platform.*
import com.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import com.github.zimoyin.qqbot.exception.HttpClientException
import com.github.zimoyin.qqbot.net.bean.Message
import com.github.zimoyin.qqbot.net.http.addRestfulParam
import com.github.zimoyin.qqbot.net.http.api.API
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.HttpRequest
import io.vertx.ext.web.client.HttpResponse
import java.util.*


/**
 * 在频道里面发送信息
 * @param group
 * @param message 消息
 */
fun HttpAPIClient.sendGroupMessage(
    group: Group,
    message: MessageChain,
): Future<MessageChain> {
    return sendGroupMessage(group, group.id, message, API.SendGroupMessage)
}


/**
 * 在频道里面发送（私）信息
 * @param channel 频道
 * @param id channelID/guildID
 * @param message 消息
 * @param client 发送消息的请求
 */
private fun HttpAPIClient.sendGroupMessage(
    group: Group,
    id: String,
    message: MessageChain,
    client: HttpRequest<Buffer>,
): Future<MessageChain> {
    val token = group.botInfo.token
    var intercept: Boolean = true
    var message0: MessageChain

    //发送前广播一个事件，该事件为 信息发送前 ChannelMessageSendPreEvent
    GroupMessageSendPreEvent(
        msgID = message.id ?: "",
        messageChain = message,
        contact = group
    ).let {
        GlobalEventBus.broadcastAuto(it)
        val result = MessageSendPreEvent.result(it)
        intercept = result.intercept
        message0 = result.messageChain
    }

    val promise = Promise.promise<MessageChain>()

    // 拦截待发送的信息,并禁止该信息发送
    if (intercept) {
        GlobalEventBus.broadcastAuto(
            MessageSendInterceptEvent(
                msgID = message.id ?: "",
                messageChain = message,
                contact = group,
            )
        )
        promise.tryFail(HttpClientException("发送消息被拦截"))
        return promise.future()
    }
    //发送信息处理
    val finalMessage = message0.convertChannelMessage().apply {
        when {
            content != null -> {
                msg_type = 0
            }

            markdown != null -> {
                msg_type = 2
            }

            ark != null -> {
                msg_type = 3
            }

            embed != null -> {
                msg_type = 4
            }

            media != null -> {
                msg_type = 7
            }
        }
    }
    val finalMessageJson = finalMessage.toJson()

    logDebug("sendGroupMessage", "发送消息: $finalMessageJson")
    //发送信息
    client.addRestfulParam(id)
        .putHeaders(token.getHeaders())
        .sendJsonObject(finalMessageJson).onFailure {
            promise.fail(it)
            logError("sendGroupMessage", "网络错误: 发送消息失败", it)
        }
        .onSuccess { resp ->
            httpSuccess(resp, group, message, promise)
        }
    return promise.future()
}

private fun HttpAPIClient.httpSuccess(
    resp: HttpResponse<Buffer>,
    group: Group,
    message: MessageChain,
    promise: Promise<MessageChain>,
) {
    kotlin.runCatching {
        resp.bodyAsJsonObject()
    }.onSuccess {
        parseJsonSuccess(it, group, message, promise)
    }.onFailure {
        logError(
            "sendGroupMessage",
            "API does not meet expectations; resp:[${resp.bodyAsString()}]",
            it
        )
        promise.fail(it)
    }
}

private fun HttpAPIClient.parseJsonSuccess(
    it: JsonObject,
    group: Group,
    message: MessageChain,
    promise: Promise<MessageChain>,
) {
    if (it.containsKey("code")) when (it.getInteger("code")) {
        304023 -> {
            //信息审核事件推送
            broadcastChannelMessageAuditEvent(it, group.botInfo)
            //发送成功广播一个事件该事件为 广播后 通过返回值获取构建事件
            val chain = kotlin.runCatching { MessageChain.convert(it.mapTo(Message::class.java)) }
                .getOrDefault(MessageChain())
            broadcastChannelMessageSendEvent(message, group, chain)
            promise.tryComplete(chain)
            logDebug("sendGroupMessage", "信息审核事件中: $it")
        }

        else -> {
            logError(
                "sendGroupMessage",
                "result -> [${it.getInteger("code")}] ${it.getString("message")}"
            )
            promise.tryFail(HttpClientException("The server does not receive this value: $it"))
        }
    } else {
        //发送成功广播一个事件该事件为 广播后 通过返回值获取构建事件
        val chain = kotlin.runCatching { MessageChain.convert(it.mapTo(Message::class.java)) }
            .getOrDefault(MessageChain())
        broadcastChannelMessageSendEvent(message, group, chain)
        promise.tryComplete(chain)
    }
}


private fun HttpAPIClient.broadcastChannelMessageAuditEvent(
    it: JsonObject,
    botInfo: BotInfo,
) {
    GlobalEventBus.broadcastAuto(
        MessageStartAuditEvent(
            metadata = it.toString(),
            botInfo = botInfo
        )
    )
}

private fun HttpAPIClient.broadcastChannelMessageSendEvent(
    message: MessageChain,
    group: Group,
    it: MessageChain,
) {
    GlobalEventBus.broadcastAuto(
        GroupMessageSendEvent(
            msgID = message.id ?: "",
            messageChain = message,
            contact = group,
            result = it
        )
    )
}
