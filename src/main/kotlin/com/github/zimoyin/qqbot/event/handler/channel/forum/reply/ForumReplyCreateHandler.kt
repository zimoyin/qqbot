package com.github.zimoyin.qqbot.event.handler.channel.forum.reply

import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.channel.forum.reply.ForumReplyCreateEvent
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import com.github.zimoyin.qqbot.net.bean.ForumReply
import com.github.zimoyin.qqbot.net.bean.Payload
import com.github.zimoyin.qqbot.utils.JSON

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
            forum = JSON.toObject<ForumReply>(payload.eventContent.toString())
        )
    }
}
