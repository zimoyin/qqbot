package com.github.zimoyin.qqbot.event.events.channel.sub

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.handler.channel.sub.SubChannelDeleteHandler
import com.github.zimoyin.qqbot.net.bean.ChannelBean


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 子频道被删除
 */
@EventAnnotation.EventMetaType("CHANNEL_DELETE")
@EventAnnotation.EventHandler(SubChannelDeleteHandler::class)
data class SubChannelDeleteEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "CHANNEL_DELETE",
    override val channelBean: ChannelBean,
) : SubChannelEvent
