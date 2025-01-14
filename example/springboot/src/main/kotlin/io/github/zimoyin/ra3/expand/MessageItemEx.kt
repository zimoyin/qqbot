package io.github.zimoyin.ra3.expand

import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import io.github.zimoyin.qqbot.bot.message.type.MessageItem

/**
 *
 * @author : zimo
 * @date : 2025/01/10
 */
fun MessageItem.toChainMessage(): MessageChain {
    return MessageChainBuilder().append(this).build()
}