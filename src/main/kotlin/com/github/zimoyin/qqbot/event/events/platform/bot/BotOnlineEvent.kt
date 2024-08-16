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
@EventAnnotation.EventMetaType("Platform_BotOnlineEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
class BotOnlineEvent(
    override val metadataType: String = "Platform_BotOnlineEvent",
    override val metadata: String,
    override val botInfo: BotInfo,
    override val eventID: String ="",
) : PlatformEvent, BotStatusEvent
