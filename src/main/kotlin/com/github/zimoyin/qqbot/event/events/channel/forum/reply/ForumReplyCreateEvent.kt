package com.github.zimoyin.qqbot.event.events.channel.forum.reply

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.handler.channel.forum.reply.ForumReplyCreateHandler
import com.github.zimoyin.qqbot.net.bean.ForumReply

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
