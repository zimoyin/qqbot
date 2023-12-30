package com.github.zimoyin.qqbot.event.events.channel.live

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.bot.contact.ChannelImpl
import com.github.zimoyin.qqbot.event.handler.channel.live.EnterChannelLiveHandler
import com.github.zimoyin.qqbot.net.websocket.bean.UserLive


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 用户进入音视频/直播子频道时
 */
@EventAnnotation.EventMetaType("AUDIO_OR_LIVE_CHANNEL_MEMBER_ENTER")
@EventAnnotation.EventHandler(EnterChannelLiveHandler::class)
data class UserEnteredChannelLiveEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "AUDIO_OR_LIVE_CHANNEL_MEMBER_ENTER",
    override val live: UserLive,
    override val channel: Channel = ChannelImpl.convert(botInfo, live.guildId!!, live.channelId, live.channelId),
) : LiveRoomEvent
