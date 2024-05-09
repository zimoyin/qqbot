package com.github.zimoyin.qqbot.bot.contact

import com.github.zimoyin.qqbot.annotation.UntestedApi
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.net.bean.message.Message
import com.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import com.github.zimoyin.qqbot.net.http.api.group.recallGroupMessage
import com.github.zimoyin.qqbot.net.http.api.group.sendGroupMessage
import io.vertx.core.Future

/**
 *
 * @author : zimo
 * @date : 2023/12/11
 */
interface Group : Contact {
    override val id: String
    override val botInfo: BotInfo

    @OptIn(UntestedApi::class)
    override fun recall(messageID: String): Future<Boolean> {
        return HttpAPIClient.recallGroupMessage(this, messageID)
    }

    override fun send(message: MessageChain): Future<MessageChain> {
        return HttpAPIClient.sendGroupMessage(this, message)
    }
}

data class GroupImpl(
    override val id: String,
    override val botInfo: BotInfo,
) : Group {

    companion object {
        fun convert(botInfo: BotInfo, message: Message) = GroupImpl(
            botInfo = botInfo,
            id = message.groupID!!
        )
    }
}
