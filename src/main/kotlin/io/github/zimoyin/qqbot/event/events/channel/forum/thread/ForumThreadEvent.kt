package io.github.zimoyin.qqbot.event.events.channel.forum.thread

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import io.github.zimoyin.qqbot.event.events.channel.forum.ForumEvent
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler
import io.github.zimoyin.qqbot.net.bean.ForumThread


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 主题事件 既创建一个全新的属于个人的帖子
 */
@EventAnnotation.EventMetaType("Not_MetaType_ForumThreadEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
interface ForumThreadEvent : ForumEvent {
    override val metadataType: String
        get() = "Not_MetaType_ForumThreadEvent"

    val forum: ForumThread
    override val channel: Channel
        get() = ChannelImpl.convert(botInfo,forum.guildId!!,forum.channelId,forum.channelId)
}
