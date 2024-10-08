package com.github.zimoyin.qqbot.event.events.bot

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.handler.BotReadyHandler
import com.github.zimoyin.qqbot.net.bean.BotUser


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("READY")
@EventAnnotation.EventHandler(BotReadyHandler::class)
data class BotReadyEvent(
    override val metadata: String,
    override val metadataType: String = "READY",
    override val botInfo: BotInfo,
    override val eventID: String ="",
    val version: Int,
    val sessionID: String,
    val user: BotUser,
    val shard: List<Int>
) : BotStatusEvent
