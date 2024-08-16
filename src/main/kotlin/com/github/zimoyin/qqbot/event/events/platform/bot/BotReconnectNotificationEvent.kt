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
 * 服务器要求机器人重连通知事件
 */
@EventAnnotation.EventMetaType("Platform_BotReconnectNotificationEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
class BotReconnectNotificationEvent(
    override val botInfo: BotInfo,
    override val metadata: String = "Platform_BotReconnectNotificationEvent",
    override val metadataType: String = "Platform_BotReconnectNotificationEvent",
    override val eventID: String ="",
) : PlatformEvent, BotStatusEvent
