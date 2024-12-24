package io.github.zimoyin.qqbot.event.handler.channel.forum.post

import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.events.channel.forum.post.ForumPostCreateEvent
import io.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import io.github.zimoyin.qqbot.net.bean.ForumPost
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.utils.JSON

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
            forum = JSON.toObject<ForumPost>(payload.eventContent.toString()),
            eventID = payload.eventID?:""
        )
    }
}
