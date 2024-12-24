package io.github.zimoyin.qqbot.event.events.channel.forum.post

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.channel.forum.post.ForumPostCreateHandler
import io.github.zimoyin.qqbot.net.bean.ForumPost


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 */
@EventAnnotation.EventMetaType("FORUM_POST_CREATE")
@EventAnnotation.EventHandler(ForumPostCreateHandler::class)
data class ForumPostCreateEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "FORUM_POST_CREATE",
    override val forum: ForumPost,
    override val eventID: String ="",
) : ForumPostEvent
