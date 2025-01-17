package io.github.zimoyin.qqbot.event.events.channel.live

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import io.github.zimoyin.qqbot.event.handler.channel.live.ExitChannelLiveHandler
import io.github.zimoyin.qqbot.net.bean.UserLive


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 用户推出音视频/直播子频道时
 */
@EventAnnotation.EventMetaType("AUDIO_OR_LIVE_CHANNEL_MEMBER_EXIT")
@EventAnnotation.EventHandler(ExitChannelLiveHandler::class)
data class UserExitChannelLiveEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "AUDIO_OR_LIVE_CHANNEL_MEMBER_EXIT",
    override val live: UserLive,
    override val eventID: String ="",
    override val channel: Channel = ChannelImpl.convert(botInfo,live.guildId!!,live.channelId,live.channelId),
) : LiveRoomEvent
