package com.github.zimoyin.qqbot.event.handler.channel.forum.post

import com.github.zimoyin.qqbot.net.websocket.bean.ForumPost
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.channel.forum.post.ForumPostCreateEvent
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler

import com.github.zimoyin.qqbot.utils.JSON
import com.github.zimoyin.qqbot.net.websocket.bean.Payload
/**
 *
 * @author : zimo
 * @date : 2023/12/08
 */
class ForumPostCreateHandler : AbsEventHandler<ForumPostCreateEvent>() {
    override fun handle(payload: Payload): ForumPostCreateEvent {
        return ForumPostCreateEvent(
            metadata = payload.metadata,
            botInfo = BotInfo.create(payload.appID!!),
            forum = JSON.toObject<ForumPost>(payload.eventContent.toString())
        )
    }
}