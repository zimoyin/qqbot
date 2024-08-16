package com.github.zimoyin.qqbot.event.events.channel.forum.post

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.handler.channel.forum.post.ForumPostDeleteHandler
import com.github.zimoyin.qqbot.net.bean.ForumPost


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 */
@EventAnnotation.EventMetaType("FORUM_POST_DELETE")
@EventAnnotation.EventHandler(ForumPostDeleteHandler::class)
data class ForumPostDeleteEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "FORUM_POST_DELETE",
    override val forum: ForumPost,
    override val eventID: String ="",
) : ForumPostEvent
