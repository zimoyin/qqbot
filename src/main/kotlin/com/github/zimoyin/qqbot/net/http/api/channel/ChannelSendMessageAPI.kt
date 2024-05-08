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
import com.github.zimoyin.qqbot.net.bean.SendMessageBean
import com.github.zimoyin.qqbot.net.http.addRestfulParam
import com.github.zimoyin.qqbot.net.http.api.API
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.HttpRequest
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.multipart.MultipartForm
import java.util.*


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

    //发送前广播一个事件，该事件为 信息发送前 ChannelMessageSendPreEvent
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

    // 拦截待发送的信息,并禁止该信息发送
    if (intercept) {
        GlobalEventBus.broadcastAuto(
            MessageSendInterceptEvent(
                msgID = message.id ?: "",
                messageChain = message,
                contact = channel,
            )
        )
        promise.tryFail(HttpClientException("发送消息被拦截"))
        return promise.future()
    }
    //发送信息处理
    val finalMessage: SendMessageBean = message0.convertChannelMessage()
    val finalMessageJson = finalMessage.toJson()


    val form = MultipartForm.create()
    finalMessageJson.forEach {
        if (it.key != null && it.value != null)
            form.attribute(it.key, it.value.toString())
    }
    if (finalMessage.channelFile != null) {
        form.binaryFileUpload(
            "file_image",
            UUID.randomUUID().toString(),
            finalMessage.channelFile.path,
            "file"
        )
    } else if (finalMessage.channelFileBytes != null) {
        form.binaryFileUpload(
            "file_image",
            UUID.randomUUID().toString(),
            Buffer.buffer(finalMessage.channelFileBytes),
            "file"
        )
    }
    if (finalMessage.audioURI != null || finalMessage.videoURI != null){
        promise.fail(IllegalArgumentException("AudioURI and videoURI cannot be used for resource sending in channels"))
        return promise.future()
    }

    logDebug("sendChannelMessageAsync", "发送消息: ${finalMessage.toStrings()}")
    //发送信息
    client.addRestfulParam(id)
        .putHeaders(token.getHeaders())
//        .sendJsonObject(finalMessageJson).onFailure {
//            promise.fail(it)
//            logError("sendChannelPrivateMessageAsync", "网络错误: 发送消息失败", it)
//        }
        .sendMultipartForm(form).onFailure {
            promise.fail(it)
            logError("sendChannelPrivateMessageAsync", "网络错误: 发送消息失败", it)
        }
        .onSuccess { resp ->
            httpSuccess(resp, channel, message, promise)
        }
    return promise.future()
}

private fun HttpAPIClient.httpSuccess(
    resp: HttpResponse<Buffer>,
    channel: Channel,
    message: MessageChain,
    promise: Promise<MessageChain>,
) {
    kotlin.runCatching {
        resp.bodyAsJsonObject()
    }.onSuccess {
        parseJsonSuccess(it, channel, message, promise)
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
    channel: Channel,
    message: MessageChain,
    promise: Promise<MessageChain>,
) {
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

        304003 ->{
            logError("sendChannelMessage", "result -> [${it.getInteger("code")}] ${it.getString("message")} : 发送的信息中出现了类似或者近似于域名的链接，请检查信息是否包含类似域名的链接，并在机器人平台报备。如果不是请将英文句号进行替换，推荐替换成‘∙’ 或者‘,’")
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
