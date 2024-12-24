package io.github.zimoyin.qqbot.event.handler.channel.forum.audit

import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.events.channel.forum.audit.ForumPublishAuditEvent
import io.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import io.github.zimoyin.qqbot.net.bean.ForumAuditResult
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.utils.JSON

/**
 *
 * @author : zimo
 * @date : 2023/12/08
 */
class ForumPublishAuditHandler : AbsEventHandler<ForumPublishAuditEvent>() {
    override fun handle(payload: Payload): ForumPublishAuditEvent {
        return ForumPublishAuditEvent(
            metadata = payload.metadata,
            botInfo = BotInfo.create(payload.appID!!),
            forum = JSON.toObject<ForumAuditResult>(payload.eventContent.toString()),
            eventID = payload.eventID?:""
        )
    }
}
