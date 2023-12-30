package com.github.zimoyin.qqbot.event.events.bot

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.event.events.Event
import com.github.zimoyin.qqbot.event.handler.BotResumedHandler

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 机器人状态变更，
 *  Hello [BotHelloEvent]
 *  上线 [BotOnlineEvent]
 *  下线 [BotOfflineEvent]
 *  重连通知 [BotReconnectNotificationEvent]
 *  重连 [BotResumedEvent]
 *  READY/第一次上线 [BotReadyEvent]
 */
@EventAnnotation.EventMetaType("Not_MetaType_BotStatusEvent")
@EventAnnotation.EventHandler(BotResumedHandler::class, true)
interface BotStatusEvent : Event