package io.github.zimoyin.qqbot.net.http.api.friend

import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Friend
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.events.MessageStartAuditEvent
import io.github.zimoyin.qqbot.event.events.platform.FriendMessageSendEvent
import io.github.zimoyin.qqbot.event.events.platform.FriendMessageSendPreEvent
import io.github.zimoyin.qqbot.event.events.platform.MessageSendInterceptEvent
import io.github.zimoyin.qqbot.event.events.platform.MessageSendPreEvent
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import io.github.zimoyin.qqbot.exception.HttpClientException
import io.github.zimoyin.qqbot.exception.HttpHandlerException
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
 * 给好友发私信
 * @param friend
 * @param message 消息
 */
fun HttpAPIClient.sendFriendMessage(
    friend: Friend,
    message: MessageChain,
): Future<SendMessageResultBean> {
    return sendFriendMessage(friend, friend.id, message, API.SendFriendMessage)
}


/**
 * 给好友发私信
 * @param friend 好友
 * @param id openid
 * @param message 消息
 * @param client 发送消息的请求
 */
private fun HttpAPIClient.sendFriendMessage(
    friend: Friend,
    id: String,
    message: MessageChain,
    client: HttpRequest<Buffer>,
): Future<SendMessageResultBean> {
    val token = friend.botInfo.token
    var intercept: Boolean = true
    var message0: MessageChain

    //发送前广播一个事件，该事件为 信息发送前 ChannelMessageSendPreEvent
    FriendMessageSendPreEvent(
        msgID = message.id ?: "", messageChain = message, contact = friend
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
                contact = friend,
            )
        )
        logDebug("sendFriendMessage", "发送消息被拦截")
        promise.tryFail(HttpClientException("发送消息被拦截"))
        return promise.future()
    }
    //发送信息处理
    val finalMessage = message0.convertChannelMessage().inferMsgType()
    if (finalMessage.msgType == SendMessageBean.MSG_TYPE_MEDIA && finalMessage.media == null) {
        uploadMediaToFriend(id, token, finalMessage.toMediaBean()).onSuccess {
            sendFriendMessage0(finalMessage, client, id, token, promise, friend, message, it)
        }.onFailure {
            logPreError(promise, "sendFirendMessage0", "上传资源到服务器失败", it).let { isLog ->
                promise.tryFail(HttpClientException("Uploading media resources to server failed", it)).apply {
                    if (!this && !isLog) logError("sendFirendMessage0", "上传资源到服务器失败", it)
                }
            }
        }
    } else {
        sendFriendMessage0(finalMessage, client, id, token, promise, friend, message)
    }
    return promise.future()
}


private fun HttpAPIClient.sendFriendMessage0(
    finalMessage: SendMessageBean,
    client: HttpRequest<Buffer>,
    id: String,
    token: Token,
    promise: Promise<SendMessageResultBean>,
    friend: Friend,
    message: MessageChain,
    mediaMsg: MediaMessageBean? = null,
) {
    // 构建适用于群聊或者单聊的消息体
    val finalMessageJson = finalMessage.apply {
        if (msgType == SendMessageBean.MSG_TYPE_MEDIA) media = mediaMsg // 媒体消息
    }.toJson().apply {
        remove("image") // 移除image字段,因为不适用于Group和 单聊
    }

//    logDebug("sendFirendMessage0", "发送消息: $finalMessageJson")
    //发送信息
    client.addRestfulParam(id).putHeaders(token.getHeaders()).sendJsonObject(finalMessageJson).onFailure {
        logPreError(promise, "sendFirendMessage0", "发送消息失败", it).let { isLog ->
            if (!promise.tryFail(it)) {
                if (!isLog) logError("sendFirendMessage0", "网络错误: 发送消息失败", it)
            }
        }

    }.onSuccess { resp ->
        httpSuccess(resp, friend, message, promise)
    }
}

/**
 * 上传媒体资源
 */
fun HttpAPIClient.uploadMediaToFriend(id: String, token: Token, mediaBean: SendMediaBean): Future<MediaMessageBean> {

    // 使用缓存
    if (MediaManager.isEnable) {
        val mediaMessageBean = MediaManager.instance[mediaBean.url]
        if (mediaMessageBean != null) {
            return Future.succeededFuture(mediaMessageBean)
        }
    }

    val promise = Promise.promise<MediaMessageBean>()
    logDebug("sendFirendMessage0", "上传媒体资源[${mediaBean.fileType}]: ${mediaBean.url}")
    API.uploadFriendMediaResource.addRestfulParam(id).putHeaders(token.getHeaders())
        .sendJsonObject(JSON.toJsonObject(mediaBean)).onSuccess {
            runCatching {
                val json = it.bodyAsJsonObject()
                if (json.getInteger("code") != null) {
                    promise.fail(HttpClientException("Upload media resource failed: $json"))
                } else {
                    logDebug(
                        "sendFirendMessage0",
                        "上传富媒体资源[${mediaBean.fileType}]: ${mediaBean.url} 成功: $json"
                    )
                }
                json.mapTo(MediaMessageBean::class.java).apply {
                    if (MediaManager.isEnable && mediaBean.url != null) MediaManager.instance[mediaBean.url] = this
                }
            }.onSuccess {
                promise.tryComplete(it)
            }.onFailure {
                logPreError(
                    promise, "sendFirendMessage0", "上传媒体资源[${mediaBean.fileType}]: ${mediaBean.url} 失败", it
                ).let { isLog ->
                    if (!promise.tryFail(it)) {
                        if (!isLog) logError(
                            "sendFirendMessage0", "上传媒体资源[${mediaBean.fileType}]: ${mediaBean.url} 失败", it
                        )
                    }
                }

            }
        }.onFailure {
            logPreError(
                promise, "sendFirendMessage0", "上传媒体资源[${mediaBean.fileType}]: ${mediaBean.url} 失败", it
            ).let { isLog ->
                if (!promise.tryFail(it)) {
                    if (!isLog) logError(
                        "sendFirendMessage0", "上传媒体资源[${mediaBean.fileType}]: ${mediaBean.url} 失败", it
                    )
                }
            }

        }
    return promise.future()
}

private fun HttpAPIClient.httpSuccess(
    resp: HttpResponse<Buffer>,
    friend: Friend,
    message: MessageChain,
    promise: Promise<SendMessageResultBean>,
) {
    kotlin.runCatching {
        resp.bodyAsJsonObject()
    }.onSuccess {
        parseJsonSuccess(it, friend, message, promise)
    }.onFailure {
        logPreError(
            promise, "sendFirendMessage0", "API does not meet expectations; resp:[${resp.bodyAsString()}]", it
        ).let { isLog ->
            if (!promise.tryFail(
                    HttpHandlerException(
                        "API does not meet expectations; resp:[${resp.bodyAsString()}]",
                        it
                    )
                )
            ) {
                if (!isLog) logError(
                    "sendChannelMessage", "API does not meet expectations; resp:[${resp.bodyAsString()}]", it
                )
            }
        }

    }
}

private fun HttpAPIClient.parseJsonSuccess(
    it: JsonObject,
    friend: Friend,
    message: MessageChain,
    promise: Promise<SendMessageResultBean>,
) {
    val result = SendMessageResultBean(
        metadata = it.toString(),
        msgID = it.getString("id") ?: it.getString("message_id"),
        contact = friend,
    )
    if (it.containsKey("code")) when (it.getInteger("code")) {
        304023, 304024 -> {
            //信息审核事件推送
            broadcastChannelMessageAuditEvent(it, friend.botInfo)
            logDebug("sendChannelMessage", "信息审核事件中: $it")
        }

        else -> {
            logPreError(
                promise, "sendFirendMessage0", "API does not meet expectations; resp:[$it]"
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
    //发送成功广播一个事件该事件为 广播后 通过返回值获取构建事件
//    val chain =
//        kotlin.runCatching { MessageChain.convert(it.mapTo(Message::class.java)) }.getOrDefault(MessageChain())
    val event = FriendMessageSendEvent(
        msgID = result.msgID ?: "",
        contact = friend,
        messageChain = message,
        result = result,
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
    event: FriendMessageSendEvent,
) {
    GlobalEventBus.broadcastAuto(event)
}
