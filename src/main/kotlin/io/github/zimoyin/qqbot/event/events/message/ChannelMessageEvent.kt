package io.github.zimoyin.qqbot.event.events.message

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.ChannelPrivateUser
import io.github.zimoyin.qqbot.bot.message.EmojiType
import io.github.zimoyin.qqbot.event.events.channel.ChannelEvent
import io.github.zimoyin.qqbot.event.handler.message.MessageHandler
import io.github.zimoyin.qqbot.net.bean.User
import io.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.github.zimoyin.qqbot.net.http.api.channel.addEmoji
import io.github.zimoyin.qqbot.net.http.api.channel.deleteEmoji
import io.github.zimoyin.qqbot.net.http.api.channel.getEmojiUserList
import io.github.zimoyin.qqbot.net.http.api.channel.recallChannelMessage
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
    override val sender: ChannelPrivateUser

    /**
     * 张贴表情
     */
    @OptIn(UntestedApi::class)
    fun addEmoji(emojiType: EmojiType): Future<Boolean> {
        return HttpAPIClient.addEmoji(channel, msgID, emojiType)
    }

    /**
     * 张贴表情
     */
    @OptIn(UntestedApi::class)
    fun addEmoji(emojiType: EmojiType, messageID: String): Future<Boolean> {
        return HttpAPIClient.addEmoji(channel, messageID, emojiType)
    }

    /**
     * 删除表情
     */
    @OptIn(UntestedApi::class)
    fun removeEmoji(emojiType: EmojiType): Future<Boolean> {
        return HttpAPIClient.deleteEmoji(channel, msgID, emojiType)
    }

    /**
     * 删除表情
     */
    @OptIn(UntestedApi::class)
    fun removeEmoji(emojiType: EmojiType, messageID: String): Future<Boolean> {
        return HttpAPIClient.deleteEmoji(channel, messageID, emojiType)
    }

    /**
     * 获取对此信息张贴表情的用户列表
     */
    @UntestedApi
    fun getEmojiList(emojiType: EmojiType): Future<List<User>> {
        return HttpAPIClient.getEmojiUserList(channel, msgID, emojiType)
    }

    /**
     * 获取对此信息张贴表情的用户列表
     */
    @UntestedApi
    fun getEmojiList(emojiType: EmojiType, messageID: String = msgID): Future<List<User>> {
        return HttpAPIClient.getEmojiUserList(channel, messageID, emojiType)
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
    fun recall(hidetip: Boolean, messageID: String): Future<Boolean> {
        return HttpAPIClient.recallChannelMessage(channel, messageID, hidetip)
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
