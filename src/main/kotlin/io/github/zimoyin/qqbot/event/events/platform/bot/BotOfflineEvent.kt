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
@EventAnnotation.EventMetaType("Platform_BotOfflineEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
class BotOfflineEvent(
    override val botInfo: BotInfo,
    val throwable: Throwable?,
    override val metadata: String = "Platform_BotOfflineEvent",
    override val metadataType: String = "Platform_BotOfflineEvent",
    override val eventID: String ="",
) : PlatformEvent, BotStatusEvent
