package com.github.zimoyin.qqbot.bot.contact

import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.net.http.api.friend.recallFriendMessage
import com.github.zimoyin.qqbot.net.http.api.friend.sendFriendMessage
import io.vertx.core.Future

/**
 * 好友
 * @author : zimo
 * @date : 2024/03/31
 */
interface Friend : Contact {
    /**
     * 好友ID & openid
     */
    override val id: String

    /**
     * 机器人信息
     */
    override val botInfo: BotInfo

    /**
     * 好友昵称
     */
    val nick: String

    /**
     * 头像
     */
    val avatar: String

    /**
     * 私聊信息撤回
     * 撤回和该朋友的聊天时，我发送的信息
     */
    @OptIn(UntestedApi::class)
    override fun recall(messageID: String): Future<Boolean> {
        return HttpAPIClient.recallFriendMessage(this, messageID)
    }

    override fun send(message: MessageChain): Future<MessageChain> {
        return HttpAPIClient.sendFriendMessage(this, message)
    }
}
