package io.github.zimoyin.qqbot.event.events.channel.sub

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.channel.sub.SubChannelUpdateHandler
import io.github.zimoyin.qqbot.net.bean.ChannelBean


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 子频道信息变更
 */
@EventAnnotation.EventMetaType("CHANNEL_UPDATE")
@EventAnnotation.EventHandler(SubChannelUpdateHandler::class)
data class SubChannelUpdateEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "CHANNEL_UPDATE",
    override val channelBean: ChannelBean,
    override val eventID: String ="",
) : SubChannelEvent
