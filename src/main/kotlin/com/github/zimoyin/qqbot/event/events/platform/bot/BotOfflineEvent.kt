package com.github.zimoyin.qqbot.event.events.platform.bot

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.bot.BotStatusEvent
import com.github.zimoyin.qqbot.event.events.platform.PlatformEvent
import com.github.zimoyin.qqbot.event.handler.NoneEventHandler

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
