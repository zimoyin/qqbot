package com.github.zimoyin.qqbot.event.events.channel.sub

import com.github.zimoyin.qqbot.net.websocket.bean.ChannelBean
import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.handler.channel.sub.SubChannelUpdateHandler


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
) : SubChannelEvent
