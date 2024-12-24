package io.github.zimoyin.qqbot.event.events.platform.bot

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.events.bot.BotStatusEvent
import io.github.zimoyin.qqbot.event.events.platform.PlatformEvent
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 机器人上线事件
 */
@EventAnnotation.EventMetaType("Platform_BotOnlineEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
class BotOnlineEvent(
    override val metadataType: String = "Platform_BotOnlineEvent",
    override val metadata: String,
    override val botInfo: BotInfo,
    override val eventID: String ="",
) : PlatformEvent, BotStatusEvent
