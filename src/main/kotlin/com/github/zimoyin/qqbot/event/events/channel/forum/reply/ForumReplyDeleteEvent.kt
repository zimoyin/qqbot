package com.github.zimoyin.qqbot.event.events.channel.forum.reply

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.handler.channel.forum.reply.ForumReplyDeleteHandler
import com.github.zimoyin.qqbot.net.bean.ForumReply

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 帖子事件
 */
@EventAnnotation.EventMetaType("FORUM_REPLY_DELETE")
@EventAnnotation.EventHandler(ForumReplyDeleteHandler::class)
data class ForumReplyDeleteEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "FORUM_REPLY_DELETE",
    override val forum: ForumReply,
) : ForumReplyEvent
