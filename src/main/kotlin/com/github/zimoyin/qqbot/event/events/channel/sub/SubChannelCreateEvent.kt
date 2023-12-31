package com.github.zimoyin.qqbot.event.events.channel.sub

import com.github.zimoyin.qqbot.net.bean.ChannelBean
import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.handler.channel.sub.SubChannelCreateHandler


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 子频道被创建
 */
@EventAnnotation.EventMetaType("CHANNEL_CREATE")
@EventAnnotation.EventHandler(SubChannelCreateHandler::class)
data class SubChannelCreateEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "CHANNEL_CREATE",
    override val channelBean: ChannelBean,
) : SubChannelEvent
