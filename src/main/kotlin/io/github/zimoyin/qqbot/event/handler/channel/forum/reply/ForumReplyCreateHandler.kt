package io.github.zimoyin.qqbot.event.handler.channel.forum.reply

import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.events.channel.forum.reply.ForumReplyCreateEvent
import io.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import io.github.zimoyin.qqbot.net.bean.ForumReply
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.utils.JSON

/**
 *
 * @author : zimo
 * @date : 2023/12/08
 */
class ForumReplyCreateHandler : AbsEventHandler<ForumReplyCreateEvent>() {
    override fun handle(payload: Payload): ForumReplyCreateEvent {
        return ForumReplyCreateEvent(
            metadata = payload.metadata,
            botInfo = BotInfo.create(payload.appID!!),
            forum = JSON.toObject<ForumReply>(payload.eventContent.toString()),
            eventID = payload.eventID?:""
        )
    }
}
