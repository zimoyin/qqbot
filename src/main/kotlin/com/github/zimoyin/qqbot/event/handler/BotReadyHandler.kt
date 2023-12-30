package com.github.zimoyin.qqbot.event.handler

import com.github.zimoyin.qqbot.net.websocket.bean.BotUser
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.bot.BotReadyEvent
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import com.github.zimoyin.qqbot.net.websocket.bean.Payload
import com.github.zimoyin.qqbot.utils.JSON

/**
 *
 * @author : zimo
 * @date : 2023/12/11
 *
 * BotResumedHandler的消息处理器
 */
class BotReadyHandler : AbsEventHandler<BotReadyEvent>() {
    override fun handle(payload: Payload): BotReadyEvent {
        val json = JSON.toJsonObject(payload.eventContent.toString())
        val info = BotInfo.create(payload.appID!!)

        return BotReadyEvent(
            metadata = payload.metadata,
            metadataType = payload.eventType!!,
            botInfo = info,
            version = json.getInteger("version"),
            sessionID = json.getString("session_id"),
            user = json.getJsonObject("user").mapTo( BotUser::class.java),
            shard = json.getJsonArray("shard").map { Integer.getInteger(it.toString()) }
        )
    }
}