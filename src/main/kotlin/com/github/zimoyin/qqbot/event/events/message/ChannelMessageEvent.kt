package com.github.zimoyin.qqbot.event.events.message

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.contact.Channel
import com.github.zimoyin.qqbot.bot.contact.ChannelUser
import com.github.zimoyin.qqbot.bot.message.EmojiType
import com.github.zimoyin.qqbot.event.events.channel.ChannelEvent
import com.github.zimoyin.qqbot.event.handler.message.MessageHandler
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.net.http.api.channel.addEmoji
import com.github.zimoyin.qqbot.net.http.api.channel.deleteEmoji
import com.github.zimoyin.qqbot.net.http.api.channel.recallChannelMessage
import io.vertx.core.Future

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("Not_MetaType_ChannelMessageEvent")
@EventAnnotation.EventHandler(MessageHandler::class, true)
interface ChannelMessageEvent : MessageEvent, ChannelEvent {
    override val windows: Channel
    override val sender: ChannelUser

    /**
     * 张贴表情
     */
    @OptIn(UntestedApi::class)
    fun addEmoji(emojiType: EmojiType): Future<Boolean> {
        return HttpAPIClient.addEmoji(channel, msgID, emojiType)
    }

    /**
     * 删除表情
     */
    @OptIn(UntestedApi::class)
    fun removeEmoji(emojiType: EmojiType): Future<Boolean> {
        return HttpAPIClient.deleteEmoji(channel, msgID, emojiType)
    }

    /**
     * 获取对此信息张贴表情的用户列表
     */
    @UntestedApi
    fun getEmojiList() {
        TODO("由于该API是需要 message_id,channel_id,type_id,type_value 作为参数并且是一个分包的API。实用性不大，故暂不实现")
    }


    /**
     * 撤回消息
     * @param hidetip 是否隐藏提示小灰条
     */
    @OptIn(UntestedApi::class)
    fun recall(hidetip: Boolean): Future<Boolean> {
        return HttpAPIClient.recallChannelMessage(channel, msgID, hidetip)
    }

    /**
     * 撤回消息
     * @param hidetip 是否隐藏提示小灰条
     */
    @OptIn(UntestedApi::class)
    fun recall(): Future<Boolean> {
        return recall(false)
    }

    companion object {
        /**
         * 张贴表情
         * @param channel 频道
         * @param msgID 消息ID
         * @param emojiType 表情类型
         *
         */
        @OptIn(UntestedApi::class)
        @JvmStatic
        fun addEmoji(channel: Channel, msgID: String, emojiType: EmojiType): Future<Boolean> {
            return HttpAPIClient.addEmoji(channel, msgID, emojiType)
        }

        /**
         * 删除表情
         * @param channel 频道
         * @param msgID 消息ID
         * @param emojiType 表情类型
         */
        @OptIn(UntestedApi::class)
        @JvmStatic
        fun deleteEmoji(channel: Channel, msgID: String, emojiType: EmojiType): Future<Boolean> {
            return HttpAPIClient.deleteEmoji(channel, msgID, emojiType)
        }
    }
}
