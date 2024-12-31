package io.github.zimoyin.qqbot.event.events.message.direct

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.ChannelPrivateUser
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.events.message.ChannelMessageEvent
import io.github.zimoyin.qqbot.event.handler.message.ChannelPrivateMessageHandler
import io.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.github.zimoyin.qqbot.net.http.api.channel.recallChannelPrivateMessage
import io.vertx.core.Future

/**
 *
 * @author : zimo
 * @date : 2023/12/11
 *
 * 频道的私信事件。私信机器人
 */
@EventAnnotation.EventMetaType("DIRECT_MESSAGE_CREATE")
@EventAnnotation.EventHandler(ChannelPrivateMessageHandler::class)
class ChannelPrivateMessageEvent(
    override val metadata: String,
    override val msgID: String,
    override val windows: Channel,
    override val messageChain: MessageChain,
    override val sender: ChannelPrivateUser,
    override val botInfo: BotInfo,
    override val channel: Channel = windows,
    override val eventID: String ="",
    override var msgSeq: Int=1,
) : PrivateMessageEvent, ChannelMessageEvent {
    override val metadataType: String = "DIRECT_MESSAGE_CREATE"

    @OptIn(UntestedApi::class)
    override fun recall(): Future<Boolean> {
        return recall(false)
    }

    @OptIn(UntestedApi::class)
    override fun recall(hidetip: Boolean): Future<Boolean> {
        return HttpAPIClient.recallChannelPrivateMessage(channel, msgID, hidetip)
    }
}
