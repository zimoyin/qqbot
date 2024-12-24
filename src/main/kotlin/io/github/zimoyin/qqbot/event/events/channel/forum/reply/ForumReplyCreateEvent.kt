package io.github.zimoyin.qqbot.event.events.channel.forum.reply

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.channel.forum.reply.ForumReplyCreateHandler
import io.github.zimoyin.qqbot.net.bean.ForumReply

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 帖子事件
 */
@EventAnnotation.EventMetaType("FORUM_REPLY_CREATE")
@EventAnnotation.EventHandler(ForumReplyCreateHandler::class)
data class ForumReplyCreateEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "FORUM_REPLY_CREATE",
    override val forum: ForumReply,
    override val eventID: String ="",
) : ForumReplyEvent
