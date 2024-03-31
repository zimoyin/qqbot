package com.github.zimoyin.qqbot.net.http.api.friend

import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.contact.Friend
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
 * @param friend
 * @param message 消息
 */
fun HttpAPIClient.sendFriendMessage(
    friend: Friend,
    message: MessageChain,
): Future<MessageChain> {
    return sendFriendMessage(friend, friend.id, message, API.SendChannelMessage)
}


/**
 * 在频道里面发送（私）信息
 * @param channel 频道
 * @param id channelID/guildID
 * @param message 消息
 * @param client 发送消息的请求
 */
private fun HttpAPIClient.sendFriendMessage(
    friend: Friend,
    id: String,
    message: MessageChain,
    client: HttpRequest<Buffer>,
): Future<MessageChain> {
    val token = friend.botInfo.token
    var intercept: Boolean = true
    var message0: MessageChain

    //发送前广播一个事件，该事件为 信息发送前 ChannelMessageSendPreEvent
    FriendMessageSendPreEvent(
        msgID = message.id ?: "",
        messageChain = message,
        contact = friend
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
                contact = friend,
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

    //发送信息
    client.addRestfulParam(id)
        .putHeaders(token.getHeaders())
        .sendJsonObject(finalMessageJson).onFailure {
            promise.fail(it)
            logError("sendFriendMessage", "网络错误: 发送消息失败", it)
        }
        .onSuccess { resp ->
            httpSuccess(resp, friend, message, promise)
        }
    return promise.future()
}

private fun HttpAPIClient.httpSuccess(
    resp: HttpResponse<Buffer>,
    friend: Friend,
    message: MessageChain,
    promise: Promise<MessageChain>,
) {
    kotlin.runCatching {
        resp.bodyAsJsonObject()
    }.onSuccess {
        parseJsonSuccess(it, friend, message, promise)
    }.onFailure {
        logError(
            "sendChannelMessage",
            "API does not meet expectations; resp:[${resp.bodyAsString()}]",
            it
        )
        promise.fail(it)
    }
}

private fun HttpAPIClient.parseJsonSuccess(
    it: JsonObject,
    friend: Friend,
    message: MessageChain,
    promise: Promise<MessageChain>,
) {
    if (it.containsKey("code")) when (it.getInteger("code")) {
        304023 -> {
            //信息审核事件推送
            broadcastChannelMessageAuditEvent(it, friend.botInfo)
            //发送成功广播一个事件该事件为 广播后 通过返回值获取构建事件
            val chain = kotlin.runCatching { MessageChain.convert(it.mapTo(Message::class.java)) }
                .getOrDefault(MessageChain())
            broadcastChannelMessageSendEvent(message, friend, chain)
            promise.tryComplete(chain)
            logDebug("sendChannelMessage", "信息审核事件中: $it")
        }

        else -> {
            logError(
                "sendChannelMessage",
                "result -> [${it.getInteger("code")}] ${it.getString("message")}"
            )
            promise.tryFail(HttpClientException("The server does not receive this value: $it"))
        }
    } else {
        //发送成功广播一个事件该事件为 广播后 通过返回值获取构建事件
        val chain = kotlin.runCatching { MessageChain.convert(it.mapTo(Message::class.java)) }
            .getOrDefault(MessageChain())
        broadcastChannelMessageSendEvent(message, friend, chain)
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
    friend: Friend,
    it: MessageChain,
) {
    GlobalEventBus.broadcastAuto(
        FriendMessageSendEvent(
            msgID = message.id ?: "",
            messageChain = message,
            contact = friend,
            result = it
        )
    )
}
