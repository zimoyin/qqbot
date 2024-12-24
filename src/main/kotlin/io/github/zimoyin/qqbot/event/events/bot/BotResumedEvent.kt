package io.github.zimoyin.qqbot.event.events.bot

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.BotResumedHandler

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("RESUMED")
@EventAnnotation.EventHandler(BotResumedHandler::class)
data class BotResumedEvent(
    override val metadata: String,
    override val metadataType: String,
    override val botInfo: BotInfo,
    override val eventID: String ="",
) : BotStatusEvent
