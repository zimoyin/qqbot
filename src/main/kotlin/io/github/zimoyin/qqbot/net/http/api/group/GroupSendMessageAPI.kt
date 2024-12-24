package io.github.zimoyin.qqbot.net.http.api.group

import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Group
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.events.MessageStartAuditEvent
import io.github.zimoyin.qqbot.event.events.platform.GroupMessageSendEvent
import io.github.zimoyin.qqbot.event.events.platform.GroupMessageSendPreEvent
import io.github.zimoyin.qqbot.event.events.platform.MessageSendInterceptEvent
import io.github.zimoyin.qqbot.event.events.platform.MessageSendPreEvent
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import io.github.zimoyin.qqbot.exception.HttpClientException
import io.github.zimoyin.qqbot.net.Token
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean
import io.github.zimoyin.qqbot.net.bean.message.send.MediaMessageBean
import io.github.zimoyin.qqbot.net.bean.message.send.SendMediaBean
import io.github.zimoyin.qqbot.net.bean.message.send.SendMessageBean
import io.github.zimoyin.qqbot.net.http.addRestfulParam
import io.github.zimoyin.qqbot.net.http.api.API
import io.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.github.zimoyin.qqbot.utils.JSON
import io.github.zimoyin.qqbot.utils.MediaManager
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.HttpRequest
import io.vertx.ext.web.client.HttpResponse


/**
 * 在群里面发送信息
 * @param group
 * @param message 消息
 */
fun HttpAPIClient.sendGroupMessage(
    group: Group,
    message: MessageChain,
): Future<SendMessageResultBean> {
    return sendGroupMessage(group, group.id, message, API.SendGroupMessage)
}


/**
 * 在群里面发送信息
 * @param group 群对象
 * @param id 群ID
 * @param message 消息
 * @param client 发送消息的请求
 */
private fun HttpAPIClient.sendGroupMessage(
    group: Group,
    id: String,
    message: MessageChain,
    client: HttpRequest<Buffer>,
): Future<SendMessageResultBean> {
    val token = group.botInfo.token
    var intercept: Boolean = true
    var message0: MessageChain

    //发送前广播一个事件，该事件为 信息发送前 ChannelMessageSendPreEvent
    GroupMessageSendPreEvent(
        msgID = message.id ?: "", messageChain = message, contact = group
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
                contact = group,
            )
        )
        logDebug("sendGroupMessage", "发送消息被拦截")
        promise.tryFail(HttpClientException("发送消息被拦截"))
        return promise.future()
    }
    //发送信息处理
    val finalMessage = message0.convertChannelMessage().inferMsgType()
//    if (finalMessage.fileBytes != null || finalMessage.file != null) {
//        logError("sendGroupMessage", "ChannelFileBytes or ChannelFile 不能在群组和私聊中使用")
//        promise.tryFail(IllegalArgumentException("ChannelFileBytes or ChannelFile cannot be used for resource sending in group chats or friends"))
//        return promise.future()
//    }
    if (finalMessage.msgType == SendMessageBean.MSG_TYPE_MEDIA && finalMessage.media == null) {
        uploadMediaToGroup(id, token, finalMessage.toMediaBean()).onSuccess {
            sendGroupMessage0(finalMessage, client, id, token, promise, group, message, it)
        }.onFailure {
            logPreError(promise, "sendGroupMessage", "上传资源到服务器失败", it).let { isLog ->
                promise.tryFail(HttpClientException("Uploading media resources to server failed", it)).apply {
                    if (!this && !isLog) logError("sendGroupMessage", "上传资源到服务器失败", it)
                }
            }

        }
    } else {
        sendGroupMessage0(finalMessage, client, id, token, promise, group, message)
    }
    return promise.future()
}

private fun HttpAPIClient.sendGroupMessage0(
    finalMessage: SendMessageBean,
    client: HttpRequest<Buffer>,
    id: String,
    token: Token,
    promise: Promise<SendMessageResultBean>,
    group: Group,
    message: MessageChain,
    mediaMsg: MediaMessageBean? = null,
) {
    // 构建适用于群聊或者单聊的消息体
    val finalMessageJson = finalMessage.apply {
        if (msgType == SendMessageBean.MSG_TYPE_MEDIA) media = mediaMsg // 媒体消息
    }.toJson().apply {
        remove("image") // 移除image字段,因为不适用于Group和 单聊
    }

    logDebug("sendGroupMessage", "发送消息: $finalMessageJson")
    //发送信息
    client.addRestfulParam(id).putHeaders(token.getHeaders()).sendJsonObject(finalMessageJson).onFailure {
        logPreError(promise, "sendGroupMessage", "发送消息失败", it).let { isLog ->
            if (!promise.tryFail(it)) {
                if (!isLog) logError("sendGroupMessage", "网络错误: 发送消息失败", it)
            }
        }

    }.onSuccess { resp ->
        httpSuccess(resp, group, message, promise)
    }
}

/**
 * 上传媒体资源
 */
fun HttpAPIClient.uploadMediaToGroup(id: String, token: Token, mediaBean: SendMediaBean): Future<MediaMessageBean> {
    if (MediaManager.isEnable) {
        val mediaMessageBean = MediaManager.instance[mediaBean.url ?: mediaBean.file_data]
        if (mediaMessageBean != null) {
            return Future.succeededFuture(mediaMessageBean)
        }
    }

    val promise = Promise.promise<MediaMessageBean>()
    logDebug("sendGroupMessage", "预备上传媒体资源[${mediaBean.fileType}]: ${mediaBean.url ?: "file_data"}")
    API.uploadGroupMediaResource.addRestfulParam(id).putHeaders(token.getHeaders())
        .sendJsonObject(JSON.toJsonObject(mediaBean)).onSuccess {
            runCatching {
                val json = it.bodyAsJsonObject()
                if (json.getInteger("code") != null) {
                    promise.fail(HttpClientException("Upload media resource failed: $json"))
                } else {
                    logDebug("sendGroupMessage", "上传富媒体资源[${mediaBean.fileType}]: ${mediaBean.url} 成功: $json")
                }
                json.mapTo(MediaMessageBean::class.java).apply {
                    if (MediaManager.isEnable && mediaBean.url != null) MediaManager.instance[mediaBean.url] = this
                }
            }.onSuccess {
                promise.tryComplete(it)
            }.onFailure {
                logPreError(
                    promise, "sendGroupMessage", "上传媒体资源[${mediaBean.fileType}]: ${mediaBean.url} 失败", it
                ).let { isLog ->
                    if (!promise.tryFail(it)) {
                        if (!isLog) logError(
                            "sendGroupMessage", "上传媒体资源[${mediaBean.fileType}]: ${mediaBean.url} 失败", it
                        )
                    }
                }

            }
        }.onFailure {
            logPreError(
                promise, "sendGroupMessage", "上传媒体资源[${mediaBean.fileType}]: ${mediaBean.url} 失败", it
            ).let { isLog ->
                if (!promise.tryFail(it)) {
                    if (!isLog) logError(
                        "sendGroupMessage", "上传媒体资源[${mediaBean.fileType}]: ${mediaBean.url} 失败", it
                    )
                }
            }

        }
    return promise.future()
}

private fun HttpAPIClient.httpSuccess(
    resp: HttpResponse<Buffer>,
    group: Group,
    message: MessageChain,
    promise: Promise<SendMessageResultBean>,
) {
    kotlin.runCatching {
        resp.bodyAsJsonObject()
    }.onSuccess {
        parseJsonSuccess(it, group, message, promise)
    }.onFailure {
        logPreError(
            promise, "sendGroupMessage", "API does not meet expectations; resp:[${resp.bodyAsString()}]"
        ).let { isLog0 ->
            promise.tryFail(it).apply {
                if (!this && !isLog0) logError(
                    "sendGroupMessage", "API does not meet expectations; resp:[${resp.bodyAsString()}]", it
                )
            }
        }

    }
}

private fun HttpAPIClient.parseJsonSuccess(
    it: JsonObject,
    group: Group,
    message: MessageChain,
    promise: Promise<SendMessageResultBean>,
) {
    val result =
        SendMessageResultBean(
            metadata = it.toString(),
            msgID = it.getString("id") ?: it.getString("message_id"),
            contact = group
        )
    if (it.containsKey("code")) when (it.getInteger("code")) {
        304023, 304024 -> {
            //信息审核事件推送
            broadcastChannelMessageAuditEvent(it, group.botInfo)
            logDebug("sendGroupMessage", "信息审核事件中: $it")
        }

        else -> {
            logPreError(
                promise, "sendGroupMessage", "result -> [${it.getInteger("code")}] ${it.getString("message")}"
            ).let { isLog ->
                promise.tryFail(HttpClientException("The server does not receive this value: $it")).apply {
                    if (!this && !isLog) logError(
                        "sendGroupMessage", "result -> [${it.getInteger("code")}] ${it.getString("message")}"
                    )
                }
            }
            return
        }
    }

    val event = GroupMessageSendEvent(
        contact = group,
        msgID = result.msgID ?: "",
        messageChain = message,
        result = result
    )
    broadcastChannelMessageSendEvent(event)
    promise.tryComplete(result)
}


private fun HttpAPIClient.broadcastChannelMessageAuditEvent(
    it: JsonObject,
    botInfo: BotInfo,
) {
    GlobalEventBus.broadcastAuto(
        MessageStartAuditEvent(
            metadata = it.toString(), botInfo = botInfo
        )
    )
}

private fun HttpAPIClient.broadcastChannelMessageSendEvent(
    event: GroupMessageSendEvent
) {
    GlobalEventBus.broadcastAuto(event)
}
