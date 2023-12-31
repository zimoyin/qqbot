package com.github.zimoyin.qqbot.event.events.channel.forum.thread

import com.github.zimoyin.qqbot.net.bean.ForumThread
import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.event.handler.channel.forum.thread.ForumThreadUpdateHandler


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 用户在话题子频道内发帖、评论、回复评论时产生该事件
 */
@EventAnnotation.EventMetaType("FORUM_THREAD_UPDATE")
@EventAnnotation.EventHandler(ForumThreadUpdateHandler::class)
data class ForumThreadUpdateEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "FORUM_THREAD_UPDATE",
    override val forum: ForumThread,
) : ForumThreadEvent
