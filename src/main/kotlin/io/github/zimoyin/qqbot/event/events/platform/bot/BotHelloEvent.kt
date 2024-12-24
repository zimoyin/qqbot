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
 * 服务器下发Hello 事件，这时候机器人刚刚请求到服务器，但是还没有完成鉴权
 */
@EventAnnotation.EventMetaType("Platform_BotHelloEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
class BotHelloEvent(
    override val botInfo: BotInfo,
    override val metadata: String = "Platform_BotHelloEvent",
    override val metadataType: String = "Platform_BotHelloEvent",
    override val eventID: String ="",
) : PlatformEvent, BotStatusEvent
