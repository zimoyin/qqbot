package com.github.zimoyin.qqbot.event.events.channel.forum.thread

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.handler.channel.forum.thread.ForumThreadDeleteHandler
import com.github.zimoyin.qqbot.net.bean.ForumThread


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 用户在话题子频道内发帖
 */
@EventAnnotation.EventMetaType("FORUM_THREAD_DELETE")
@EventAnnotation.EventHandler(ForumThreadDeleteHandler::class)
data class ForumThreadDeleteEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "FORUM_THREAD_DELETE",
    override val forum: ForumThread,
) : ForumThreadEvent
