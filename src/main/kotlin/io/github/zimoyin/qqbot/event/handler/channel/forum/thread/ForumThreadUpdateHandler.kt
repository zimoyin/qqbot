package io.github.zimoyin.qqbot.event.handler.channel.forum.thread


import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.events.channel.forum.thread.ForumThreadUpdateEvent
import io.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import io.github.zimoyin.qqbot.net.bean.ForumThread
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.utils.JSON

/**
 *
 * @author : zimo
 * @date : 2023/12/08
 */
class ForumThreadUpdateHandler : AbsEventHandler<ForumThreadUpdateEvent>() {
    override fun handle(payload: Payload): ForumThreadUpdateEvent {
        return ForumThreadUpdateEvent(
            metadata = payload.metadata,
            botInfo = BotInfo.create(payload.appID!!),
            forum = JSON.toObject<ForumThread>(payload.eventContent.toString()),
            eventID = payload.eventID?:""
        ).apply {
            this.forum.channel = channel
        }
    }
}
