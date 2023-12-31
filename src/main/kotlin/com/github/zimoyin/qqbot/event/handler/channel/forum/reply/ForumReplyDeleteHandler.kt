package com.github.zimoyin.qqbot.event.handler.channel.forum.reply

import com.github.zimoyin.qqbot.net.bean.ForumReply
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.channel.forum.reply.ForumReplyDeleteEvent
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler

import com.github.zimoyin.qqbot.utils.JSON
import com.github.zimoyin.qqbot.net.bean.Payload
/**
 *
 * @author : zimo
 * @date : 2023/12/08
 */
class ForumReplyDeleteHandler : AbsEventHandler<ForumReplyDeleteEvent>() {
    override fun handle(payload: Payload): ForumReplyDeleteEvent {
        return ForumReplyDeleteEvent(
            metadata = payload.metadata,
            botInfo = BotInfo.create(payload.appID!!),
            forum = JSON.toObject<ForumReply>(payload.eventContent.toString())
        )
    }
}
