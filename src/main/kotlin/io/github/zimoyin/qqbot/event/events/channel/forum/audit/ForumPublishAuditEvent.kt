package io.github.zimoyin.qqbot.event.events.channel.forum.audit

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import io.github.zimoyin.qqbot.event.events.channel.forum.ForumEvent
import io.github.zimoyin.qqbot.event.handler.channel.forum.audit.ForumPublishAuditHandler
import io.github.zimoyin.qqbot.net.bean.ForumAuditResult


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 * 帖子审核事件
 */
@EventAnnotation.EventMetaType("FORUM_PUBLISH_AUDIT_RESULT")
@EventAnnotation.EventHandler(ForumPublishAuditHandler::class)
data class ForumPublishAuditEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "FORUM_PUBLISH_AUDIT_RESULT",
    override val eventID: String ="",
    val forum: ForumAuditResult,
    override val channel: Channel = ChannelImpl.convert(botInfo, forum.guildId!!, forum.channelId, forum.channelId),
) : ForumEvent
