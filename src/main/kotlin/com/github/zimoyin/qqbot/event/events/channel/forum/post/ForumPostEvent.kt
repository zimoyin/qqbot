package com.github.zimoyin.qqbot.event.events.channel.forum.post

import com.github.zimoyin.qqbot.net.websocket.bean.ForumPost
import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.bot.contact.ChannelImpl
import com.github.zimoyin.qqbot.event.events.channel.forum.ForumEvent
import com.github.zimoyin.qqbot.event.handler.NoneEventHandler

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 帖子事件
 * TODO 该事件以及子事件未经任何测试
 */
@EventAnnotation.EventMetaType("Not_MetaType_ForumPostEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
interface ForumPostEvent : ForumEvent {
    override val metadataType: String
        get() = "Not_MetaType_ForumPostEvent"

    /**
     * 话题频道内对帖子主题评论或删除时生产事件中包含该对象
     */
    val forum: ForumPost

    override val channel: Channel
        get() = ChannelImpl.convert(botInfo,forum.guildId!!,forum.channelId,forum.channelId)
}