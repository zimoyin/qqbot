package com.github.zimoyin.qqbot.event.events.channel.forum.audit

import com.github.zimoyin.qqbot.net.bean.ForumAuditResult
import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.bot.contact.ChannelImpl
import com.github.zimoyin.qqbot.event.events.channel.forum.ForumEvent
import com.github.zimoyin.qqbot.event.handler.channel.forum.audit.ForumPublishAuditHandler


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
    val forum: ForumAuditResult,
    override val channel: Channel = ChannelImpl.convert(botInfo, forum.guildId!!, forum.channelId, forum.channelId),
) : ForumEvent
