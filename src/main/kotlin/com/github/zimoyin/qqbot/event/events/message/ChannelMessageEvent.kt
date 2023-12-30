package com.github.zimoyin.qqbot.event.events.message

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.event.events.channel.ChannelEvent
import com.github.zimoyin.qqbot.event.handler.message.MessageHandler

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("Not_MetaType_ChannelMessageEvent")
@EventAnnotation.EventHandler(MessageHandler::class, true)
interface ChannelMessageEvent : MessageEvent, ChannelEvent {
    override val windows: Channel

    fun addEmoji(){
        //TODO 张贴表情
    }

    fun removeEmoji(){
        //TODO 删除表情
    }

    fun getEmojiList(){
        //TODO 获取对此信息张贴表情的用户列表
    }

    //TODO 提供静态方法，对外暴露以上的通用的调用方法
}