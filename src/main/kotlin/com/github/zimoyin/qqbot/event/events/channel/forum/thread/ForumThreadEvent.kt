package com.github.zimoyin.qqbot.event.events.channel.forum.thread

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.bot.contact.ChannelImpl
import com.github.zimoyin.qqbot.event.events.channel.forum.ForumEvent
import com.github.zimoyin.qqbot.event.handler.NoneEventHandler
import com.github.zimoyin.qqbot.net.websocket.bean.ForumThread


/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 主题事件 既创建一个全新的属于个人的帖子
 * TODO 该事件以及子事件未经任何测试
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