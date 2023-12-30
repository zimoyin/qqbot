package com.github.zimoyin.qqbot.bot.contact

import com.github.zimoyin.qqbot.net.websocket.bean.Message
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.message.MessageChain
import io.vertx.core.Future

/**
 *
 * @author : zimo
 * @date : 2023/12/11
 */
interface Group : Contact {
    override val id: String
    override val botInfo: BotInfo
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
    override fun send(message: MessageChain): Future<MessageChain> {
        TODO("Not yet implemented")
    }



}