package com.github.zimoyin.qqbot.net.http.api.channel

import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.event.events.MessageStartAuditEvent
import com.github.zimoyin.qqbot.event.events.platform.ChannelMessageSendEvent
import com.github.zimoyin.qqbot.event.events.platform.ChannelMessageSendPreEvent
import com.github.zimoyin.qqbot.event.events.platform.MessageSendInterceptEvent
import com.github.zimoyin.qqbot.event.events.platform.MessageSendPreEvent
import com.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import com.github.zimoyin.qqbot.exception.HttpClientException
import com.github.zimoyin.qqbot.net.bean.Message
import com.github.zimoyin.qqbot.net.http.addRestfulParam
import com.github.zimoyin.qqbot.net.http.api.API
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.utils.JSON
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.HttpRequest


/**
 *
 * @author : zimo
 * @date : 2023/12/21
 */
/**
 * 在频道里面发送信息
 * @param channel 频道
 * @param message 消息
 */
fun HttpAPIClient.sendChannelMessageAsync(
    channel: Channel,
    message: MessageChain,
): Future<MessageChain> {
    return sendChannelMessageAsync0(channel, channel.channelID!!, message, API.SendChannelMessage)
}

/**
 * 在频道里面发送私信信息
 * @param channel 频道
 * @param message 消息
 */
fun HttpAPIClient.sendChannelPrivateMessageAsync(
    channel: Channel,
    message: MessageChain,
): Future<MessageChain> {
    return sendChannelMessageAsync0(channel, channel.guildID, message, API.SendChannelPrivateMessage)
}


/**
 * 在频道里面发送（私）信息
 * @param channel 频道
 * @param id channelID/guildID
 * @param message 消息
 * @param client 发送消息的请求
 */
private fun HttpAPIClient.sendChannelMessageAsync0(
    channel: Channel,
    id: String,
    message: MessageChain,
    client: HttpRequest<Buffer>,
): Future<MessageChain> {
    val token = channel.botInfo.token
    var intercept: Boolean = true
    var message0: MessageChain
    //发送前广播一个事件，该事件为 广播前
    ChannelMessageSendPreEvent(
        msgID = message.id ?: "",
        messageChain = message,
        contact = channel
    ).let {
        GlobalEventBus.broadcastAuto(it)
        val result = MessageSendPreEvent.result(it)
        intercept = result.intercept
        message0 = result.messageChain
    }

    val promise = Promise.promise<MessageChain>()
    if (intercept) {
        GlobalEventBus.broadcastAuto(
            MessageSendInterceptEvent(
                msgID = message.id ?: "",
                messageChain = message,
                contact = channel,
            )
        )
        promise.tryComplete()
        return promise.future()
    }
    //发送信息
    client.addRestfulParam(id)
        .putHeaders(token.getHeaders())
        .sendJsonObject(JSON.toJsonObject(message0.convertChannelMessage())).onFailure {
            promise.fail(it)
            logError("sendChannelPrivateMessageAsync", "网络错误: 发送消息失败", it)
        }.onSuccess { resp ->
            kotlin.runCatching {
                resp.bodyAsJsonObject()
            }.onSuccess {
                if (it.containsKey("code")) when (it.getInteger("code")) {
                    304023 -> {
                        //信息审核事件推送
                        broadcastChannelMessageAuditEvent(it, channel)
                        //发送成功广播一个事件该事件为 广播后 通过返回值获取构建事件
                        val chain = kotlin.runCatching { MessageChain.convert(it.mapTo(Message::class.java)) }
                            .getOrDefault(MessageChain())
                        broadcastChannelMessageSendEvent(message, channel, chain)
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
                    broadcastChannelMessageSendEvent(message, channel, chain)
                    promise.tryComplete(chain)
                }
            }.onFailure {
                logError(
                    "sendChannelMessage",
                    "API does not meet expectations; resp:[${resp.bodyAsString()}]",
                    it
                )
                promise.fail(it)
            }
        }
    return promise.future()
}


private fun HttpAPIClient.broadcastChannelMessageAuditEvent(
    it: JsonObject,
    channel: Channel,
) {
    GlobalEventBus.broadcastAuto(
        MessageStartAuditEvent(
            metadata = it.toString(),
            botInfo = channel.botInfo
        )
    )
}

private fun HttpAPIClient.broadcastChannelMessageSendEvent(
    message: MessageChain,
    channel: Channel,
    it: MessageChain,
) {
    GlobalEventBus.broadcastAuto(
        ChannelMessageSendEvent(
            msgID = message.id ?: "",
            messageChain = message,
            contact = channel,
            result = it
        )
    )
}
