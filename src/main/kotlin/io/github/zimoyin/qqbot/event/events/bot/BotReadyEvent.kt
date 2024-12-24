package io.github.zimoyin.qqbot.event.events.bot

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.BotReadyHandler
import io.github.zimoyin.qqbot.net.bean.BotUser


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
