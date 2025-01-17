package io.github.zimoyin.qqbot.net.http.api.channel

import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.events.MessageStartAuditEvent
import io.github.zimoyin.qqbot.event.events.platform.ChannelMessageSendEvent
import io.github.zimoyin.qqbot.event.events.platform.ChannelMessageSendPreEvent
import io.github.zimoyin.qqbot.event.events.platform.MessageSendInterceptEvent
import io.github.zimoyin.qqbot.event.events.platform.MessageSendPreEvent
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import io.github.zimoyin.qqbot.exception.HttpClientException
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean
import io.github.zimoyin.qqbot.net.bean.message.send.SendMediaBean
import io.github.zimoyin.qqbot.net.bean.message.send.SendMessageBean
import io.github.zimoyin.qqbot.net.http.addRestfulParam
import io.github.zimoyin.qqbot.net.http.api.API
import io.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.impl.HttpClientInternal
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
): Future<SendMessageResultBean> {
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
): Future<SendMessageResultBean> {
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
): Future<SendMessageResultBean> {
    val token = channel.botInfo.token
    var intercept: Boolean = true
    var message0: MessageChain

    //发送前广播一个事件，该事件为 信息发送前 ChannelMessageSendPreEvent
    ChannelMessageSendPreEvent(
        msgID = message.id ?: "", messageChain = message, contact = channel
    ).let {
        GlobalEventBus.broadcastAuto(it)
        val result = MessageSendPreEvent.result(it)
        intercept = result.intercept
        message0 = result.messageChain
    }

    val promise = Promise.promise<SendMessageResultBean>()

    // 拦截待发送的信息,并禁止该信息发送
    if (intercept) {
        GlobalEventBus.broadcastAuto(
            MessageSendInterceptEvent(
                msgID = message.id ?: "",
                messageChain = message,
                contact = channel,
            )
        )
        logDebug("sendChannelMessageAsync0", "发送信息被拦截")
        promise.tryFail(HttpClientException("发送消息被拦截"))
        return promise.future()
    }
    //发送信息处理
    val finalMessage: SendMessageBean = message0.convertSendMessageBean()
    val finalMessageJson = finalMessage.toJson()


    val form = MultipartForm.create()
    finalMessageJson.forEach {
        if (it.key == "msg_seq" || it.key == "msg_type") return@forEach
        if (it.key != null && it.value != null && it.value.toString() != "") form.attribute(it.key, it.value.toString())
    }

    if (finalMessage.file != null) {
        form.binaryFileUpload(
            "file_image", finalMessage.file.name, finalMessage.file.path, "file"
        )
    } else if (finalMessage.fileBytes != null) {
        form.binaryFileUpload(
            "file_image", UUID.randomUUID().toString(), Buffer.buffer(finalMessage.fileBytes), "file"
        )
    }

    if (finalMessage.fileType == SendMediaBean.FILE_TYPE_VIDEO || finalMessage.fileType == SendMediaBean.FILE_TYPE_AUDIO) {
        logError("sendChannelMessageAsync", "AudioURI 和 videoURI 不能在频道中使用")
        promise.tryFail(IllegalArgumentException("AudioURI and videoURI cannot be used for resource sending in channels"))
        return promise.future()
    }

//    logDebug("sendChannelMessageAsync", "发送消息: ${finalMessage.toStrings().replace("\n", "\\n")}")
    //发送信息
    val client0 = client.addRestfulParam(id).putHeaders(token.getHeaders())
    if (finalMessage.file == null && finalMessage.fileBytes == null) {
        logDebug("sendChannelMessageAsync", "以JSON形式发送信息")
        logDebug("sendChannelMessageAsync", "发送消息: $finalMessageJson")
        // JSON 发生方式，markdown 参数在 from 方式下无法被服务器正确的解析
        client0.sendJsonObject(finalMessageJson).onFailure {
            logPreError(promise, "sendChannelMessageAsync0", "网络错误: 发送消息失败", it).let { isLog ->
                if (!promise.tryFail(it)) {
                    if (!isLog) logError("sendChannelMessageAsync0", "网络错误: 发送消息失败", it)
                }
            }
        }
    } else {
        logDebug("sendChannelMessageAsync", "以MultipartForm形式发生信息")
        logDebug("sendChannelMessageAsync", "发送消息(还原JSON): $finalMessageJson")
        if (finalMessageJson.getString("message_reference") != null) {
            logError("sendChannelMessageAsync0", "在频道发送本地图片时发送引用消息会导致无法发送")
        }
        if (finalMessageJson.getString("markdown") != null) {
            logDebug("sendChannelMessageAsync0", "在频道发送本地图片时发送MD消息可能会导致无法发送")
        }
        if (finalMessageJson.getString("ark") != null) {
            logDebug("sendChannelMessageAsync0", "在频道发送本地图片时发送ARK消息可能会导致无法发送")
        }
        if (finalMessageJson.getString("embed") != null) {
            logDebug("sendChannelMessageAsync0", "在频道发送本地图片时发送EMBED消息可能会导致无法发送")
        }

        client0.sendMultipartForm(form).onFailure {
            logPreError(promise, "sendChannelMessageAsync0", "网络错误: 发送消息失败", it).let { isLog ->
                if (!promise.tryFail(it)) {
                    if (!isLog) logError("sendChannelMessageAsync0", "网络错误: 发送消息失败", it)
                }
            }
        }
    }.onSuccess { resp ->
        httpSuccess(resp, channel, message, promise)
    }

    return promise.future()
}

private fun HttpAPIClient.httpSuccess(
    resp: HttpResponse<Buffer>,
    channel: Channel,
    message: MessageChain,
    promise: Promise<SendMessageResultBean>,
) {
    kotlin.runCatching {
        resp.bodyAsJsonObject()
    }.onSuccess {
        parseJsonSuccess(it, channel, message, promise)
    }.onFailure {
        logPreError(
            promise, "sendChannelMessage", "API does not meet expectations; resp:[${resp.bodyAsString()}]"
        ).let { isLog ->
            if (!promise.tryFail(it)) {
                if (!isLog) logError(
                    "sendChannelMessage", "API does not meet expectations; resp:[${resp.bodyAsString()}]", it
                )
            }
        }

    }
}

private fun HttpAPIClient.parseJsonSuccess(
    it: JsonObject,
    channel: Channel,
    message: MessageChain,
    promise: Promise<SendMessageResultBean>,
) {
    val result =
        SendMessageResultBean(
            metadata = it.toString(),
            msgID = it.getString("id") ?: it.getString("message_id"),
            contact = channel
        )
    if (it.containsKey("code")) when (it.getInteger("code")) {
        304023, 304024 -> {
            //信息审核事件推送
            broadcastChannelMessageAuditEvent(it, channel)
            logDebug("sendChannelMessage", "信息审核事件中: $it")
        }

        304003 -> {
            logPreError(promise, "sendChannelMessage", "发送信息中包含链接").let { isLog ->
                promise.tryFail(HttpClientException("The server does not receive this value: $it")).apply {
                    if (!this && !isLog) logError(
                        "sendChannelMessage",
                        "result -> [${it.getInteger("code")}] ${it.getString("message")} : 发送的信息中出现了类似或者近似于域名的链接，请检查信息是否包含类似域名的链接，并在机器人平台报备。如果不是请将英文句号进行替换，推荐替换成‘∙’ 或者‘,’"
                    )
                }
            }
            return
        }

        else -> {
            logPreError(
                promise, "sendChannelMessage", "result -> [${it.getInteger("code")}] ${it.getString("message")}"
            ).let { isLog ->
                promise.tryFail(HttpClientException("The server does not receive this value: $it")).apply {
                    if (!this && !isLog) logError(
                        "sendChannelMessage", "result -> [${it.getInteger("code")}] ${it.getString("message")}"
                    )
                }
            }
            return
        }
    }

    val event = ChannelMessageSendEvent(
        contact = channel,
        msgID = result.msgID ?: "",
        messageChain = message,
        result = result
    )
    broadcastChannelMessageSendEvent(event)
    promise.tryComplete(result)
}


private fun HttpAPIClient.broadcastChannelMessageAuditEvent(
    it: JsonObject,
    channel: Channel,
) {
    GlobalEventBus.broadcastAuto(
        MessageStartAuditEvent(
            metadata = it.toString(), botInfo = channel.botInfo
        )
    )
}

private fun HttpAPIClient.broadcastChannelMessageSendEvent(
    event: ChannelMessageSendEvent
) {
    GlobalEventBus.broadcastAuto(event)
}
