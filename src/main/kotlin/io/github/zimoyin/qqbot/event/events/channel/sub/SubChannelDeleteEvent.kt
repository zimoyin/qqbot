package io.github.zimoyin.qqbot.event.events.channel.sub

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.channel.sub.SubChannelDeleteHandler
import io.github.zimoyin.qqbot.net.bean.ChannelBean


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
    override val eventID: String ="",
) : SubChannelEvent
