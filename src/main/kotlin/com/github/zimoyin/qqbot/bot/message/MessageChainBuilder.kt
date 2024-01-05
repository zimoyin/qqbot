package com.github.zimoyin.qqbot.bot.message

import com.github.zimoyin.qqbot.bot.message.type.MessageItem
import com.github.zimoyin.qqbot.bot.message.type.PlainTextMessage
import com.github.zimoyin.qqbot.bot.message.type.ReferenceMessage
import java.util.*

/**
 * 信息链构造器
 * 该构造器主要可以构建以下信息类型。TODO 暂时不考虑 群里与单聊
 * 1. 纯文本
 * 2. 图文混排 TODO 群里与单聊
 * 5. media 富媒体 TODO 单聊/群聊
 */
class MessageChainBuilder(private var id: String? = null) {
    constructor(chain: MessageChain) : this(chain.id) {
        append(chain)
    }

    private val internalItems: ArrayList<MessageItem> = ArrayList()

    fun setID(id: String): MessageChainBuilder {
        this.id = id
        return this
    }

    fun getID(): String {
        return id!!
    }

    fun reference(id: String): MessageChainBuilder {
        internalItems.add(ReferenceMessage(id))
        return this
    }

    fun reference(chain: MessageChain): MessageChainBuilder {
        internalItems.add(ReferenceMessage(chain.id!!))
        return this
    }

    fun append(item: MessageItem): MessageChainBuilder {
        internalItems.add(item)
        return this
    }

    fun append(text: String): MessageChainBuilder {
        internalItems.add(PlainTextMessage(text))
        return this
    }

    fun append(chain: MessageChain): MessageChainBuilder {
        chain.forEach {
            internalItems.add(it)
        }
        return this
    }

    fun buildMetaTextContent(): String {
        val sb = StringBuilder()
        internalItems.forEach {
            sb.append(it.toMetaContent())
        }
        return sb.toString()
    }

    fun build(): MessageChain {
        return MessageChain(id = id, metaTextContent = buildMetaTextContent(), internalItems = internalItems)
    }
}
