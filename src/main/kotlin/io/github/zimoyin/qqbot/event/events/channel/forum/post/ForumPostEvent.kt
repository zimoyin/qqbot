package io.github.zimoyin.qqbot.event.events.channel.forum.post

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import io.github.zimoyin.qqbot.event.events.channel.forum.ForumEvent
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler
import io.github.zimoyin.qqbot.net.bean.ForumPost

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 帖子事件
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
