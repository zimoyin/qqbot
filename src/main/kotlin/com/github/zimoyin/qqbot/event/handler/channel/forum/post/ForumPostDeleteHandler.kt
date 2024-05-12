package com.github.zimoyin.qqbot.event.handler.channel.forum.post

import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.events.channel.forum.post.ForumPostDeleteEvent
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import com.github.zimoyin.qqbot.net.bean.ForumPost
import com.github.zimoyin.qqbot.net.bean.Payload
import com.github.zimoyin.qqbot.utils.JSON

/**
 *
 * @author : zimo
 * @date : 2023/12/08
 */
class ForumPostDeleteHandler : AbsEventHandler<ForumPostDeleteEvent>() {
    override fun handle(payload: Payload): ForumPostDeleteEvent {
        return ForumPostDeleteEvent(
            metadata = payload.metadata,
            botInfo = BotInfo.create(payload.appID!!),
            forum = JSON.toObject<ForumPost>(payload.eventContent.toString())
        )
    }
}
