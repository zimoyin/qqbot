package com.github.zimoyin.qqbot.bot.message


import com.github.zimoyin.qqbot.net.bean.*
import com.github.zimoyin.qqbot.net.websocket.bean.*
import com.github.zimoyin.qqbot.utils.JSON
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.time.Instant
import java.util.*
import java.util.stream.Stream

/**
 *
 * @author : zimo
 * @date : 2023/12/09
 *
 */
class MessageChain(
    /**
     * 信息ID，如果需要构造信息发送的话请注意以下内容:
     * 主动消息：发送消息时，未填充 msg_id 字段的消息。
     * 被动消息：发送消息时，填充了 msg_id 字段的消息。接口使用此 msg_id 拉取用户的消息，同时判断用户消息的发送时间，如果超过被动消息回复时效，将会不允许发送该消息。
     */
    val id: String? = null,
    /**
     * 当前信息创建的时间
     */
    val timestamp: Instant = Instant.now(),
    val editedTimestamp: Instant = Instant.now(),
    val metaTextContent: String? = null,

    private val internalItems: ArrayList<MessageItem> = ArrayList(),
) : Serializable, Cloneable, Iterable<MessageItem> {
    companion object {
        /**
         * 将bean 转为 信息集
         */
        fun convert(message0: Message): MessageChain = MessageChain(
            id = message0.msgID!!,
            timestamp = message0.timestamp ?: Instant.now(),
            editedTimestamp = message0.editedTimestamp ?: Instant.now(),
            metaTextContent = message0.content,
        ).apply {
            internalItems.apply {
                metaTextContent?.let {
                    add(PlainText(it))
                }
                //处理 AT 信息
                // 分割全体成员
                splitAndAdd("@everyone", AtALL("everyone"))
                // 分割频道主
                splitAndAdd("@频道主", AtChannelOwnerAll("频道主"))
                // 分割在线成员
                splitAndAdd("@在线成员", AtOnlineAll("在线成员"))
                // 分割at成员
                message0.mentions?.forEach { user ->
                    splitAndAdd(
                        "<@!${user.uid}>", At(
                            id = user.uid,
                            name = user.username!!,
                            avatar = user.avatar!!,
                            isBot = user.isBot!!,
                            unionOpenID = user.unionOpenID,
                            unionUserAccount = user.unionUserAccount,
                        )
                    )
                }
                //处理表情 <emoji:/id>
                if (metaTextContent != null && metaTextContent.contains("<emoji:\\d+>".toRegex())) {
                    val numbers = Regex("<emoji:(\\d+)>")
                        .findAll(metaTextContent)
                        .map { it.groupValues[1].toInt() }
                        .toMutableList()
                    EmojiType.entries.forEach {
                        if (metaTextContent.contains("<emoji:${it.id}>")) {
                            splitAndAdd("<emoji:${it.id}>", Emoji(it.id.toString(), it))
                            numbers.remove(it.id)
                        }
                    }
                    if (numbers.size > 0) LoggerFactory.getLogger(MessageChain::class.java)
                        .warn("未找到表情ids: $numbers ")
                    numbers.forEach {
                        splitAndAdd("<emoji:${it}>", Emoji(it.toString(), EmojiType.NULL))
                    }
                }
                //处理文件与相册
                message0.attachments?.forEach {
                    if (it.contentType != null && it.contentType.contains("image")) {
                        add(Image(it.filename, it))
                    } else {
                        add(File(it.filename, it))
                    }
                }
                //处理 MessageReference
                if (message0.messageReference != null) add(ReferenceMessage(message0.messageReference.messageId!!))
                //处理 embeds
                if (message0.embeds != null) {
                    message0.embeds.forEach {
                        add(Embed(it))
                    }
                }
                //处理 MessageArk
                if (message0.ark != null) add(Ark(message0.ark))
            }


        }

        fun builder(): MessageChainBuilder {
            return MessageChainBuilder()
        }

        fun builder(chain: MessageChain): MessageChainBuilder {
            return MessageChainBuilder(chain.id)
        }

        fun builder(id: String): MessageChainBuilder {
            return MessageChainBuilder(id)
        }
    }


    override fun iterator(): Iterator<MessageItem> {
        return internalItems.iterator()
    }

    fun get(index: Int): MessageItem = internalItems[index]

    inline fun <reified T : MessageItem> get(): ArrayList<T> {
        val list = ArrayList<T>()
        toList().forEach {
            if (it is T) list.add(it)
        }
        return list
    }


    fun indexOf(item: MessageItem): Int {
        return internalItems.indexOf(item)
    }

    fun lastIndexOf(item: MessageItem): Int {
        return internalItems.lastIndexOf(item)
    }

    fun size(): Int {
        return internalItems.size
    }

    fun isEmpty(): Boolean {
        return internalItems.isEmpty()
    }

    fun contains(item: MessageItem): Boolean {
        return internalItems.contains(item)
    }

    fun toArray(): Array<MessageItem> {
        return internalItems.toTypedArray()
    }

    fun containsAll(items: Collection<MessageItem>): Boolean {
        return internalItems.containsAll(items)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MessageChain) return false

        if (id != other.id) return false
        if (internalItems != other.internalItems) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + internalItems.hashCode()
        return result
    }

    fun stream(): Stream<MessageItem> {
        return internalItems.stream()
    }

    fun spliterator(): Spliterator<MessageItem> {
        return internalItems.spliterator()
    }

    fun toList(): List<MessageItem> {
        return internalItems.toList()
    }

    fun content(): String {
        val sb = StringBuilder()
        internalItems.forEach {
            sb.append(it.toContent())
        }
        return sb.toString()
    }

    override fun toString(): String {
        val sb = ArrayList<String>()
        internalItems.forEach {
            sb.add(it.toStringType())
        }
        return sb.toString()
    }


    private fun ArrayList<MessageItem>.splitAndAdd(
        delimiter: String,
        wendItem: MessageItem? = null,
    ) {
        val temp = ArrayList<MessageItem>()
        forEach { msg ->
            if (msg is PlainText) {
                msg.content.customSplit(delimiter).forEach {
                    if (it == delimiter) {
                        wendItem?.let { temp.add(wendItem) }
                    } else {
                        temp.add(PlainText(it))
                    }
                }
            } else {
                temp.add(msg)
            }
        }
        clear()
        addAll(temp)
    }

    private fun String.customSplit(delimiter: String): List<String> {
        val input = this
        val resultList = mutableListOf<String>()

        var startIndex = 0
        var nextIndex: Int

        do {
            nextIndex = input.indexOf(delimiter, startIndex)

            if (nextIndex != -1) {
                if (startIndex != nextIndex) resultList.add(input.substring(startIndex, nextIndex))
                resultList.add(delimiter)
                startIndex = nextIndex + delimiter.length
            } else {
                resultList.add(input.substring(startIndex))
            }
        } while (nextIndex != -1 && startIndex < input.length)

        return resultList
    }


    /**
     * 将 MessageChain 转换为 ChannelMessage。用于发送消息
     */
    fun convertChannelMessage(): SendMessageBean {
        val reference = internalItems.filterIsInstance<ReferenceMessage>().lastOrNull()?.let { MessageReference(it.id) }
        val sb = StringBuilder()
        internalItems.forEach {
            if (it is PlainText) sb.append(it.toMetaContent())
            if (it is Emoji) sb.append(it.toMetaContent())
            if (it is At) sb.append(it.toMetaContent())
            if (it is AtALL) sb.append(it.toMetaContent())
            if (it is AtOnlineAll) sb.append(it.toMetaContent())
            if (it is AtChannelOwnerAll) sb.append(it.toMetaContent())
        }
        val image = internalItems.filterIsInstance<Image>().lastOrNull()?.attachment?.getURL()
        val ark = internalItems.filterIsInstance<Ark>().lastOrNull()?.ark
        val embed = internalItems.filterIsInstance<Embed>().lastOrNull()?.embed
        val md = internalItems.filterIsInstance<Markdown>().lastOrNull()?.markdown
        return SendMessageBean(
            id = this.id,
            messageReference = reference,
            content = if (sb.isEmpty()) null else sb.toString(),
            image = image,
            ark = ark, //TODO 未构建与测试[权限不足]
            embed = embed, //TODO 未构建与测试[权限不足]
            markdown = md //TODO 未构建与测试[权限不足]
        )
    }
    //TODO 单聊/群聊 -> 信息bean 构建
}

/**
 * 信息链构造器
 * 该构造器主要可以构建以下信息类型。TODO 暂时不考虑 群里与单聊
 * 1. 纯文本
 * 2. 图文混排 TODO 群里与单聊
 * 5. media 富媒体 TODO 单聊/群聊
 */
class MessageChainBuilder(val id: String? = null) {
    private val internalItems: ArrayList<MessageItem> = ArrayList()
    fun append(item: MessageItem): MessageChainBuilder {
        internalItems.add(item)
        return this
    }

    fun append(text: String): MessageChainBuilder {
        internalItems.add(PlainText(text))
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

interface MessageItem : Serializable {
    /**
     * 获取消息内容
     */
    fun toContent(): String {
        return ""
    }

    /**
     * 信息类型与部分信息
     */
    fun toStringType(): String {
        return toString()
    }

    /**
     * 构建消息元信息
     */
    fun toMetaContent(): String {
        return ""
    }
}


data class PlainText(val content: String) : MessageItem {
    override fun toContent(): String {
        return content
    }

    override fun toStringType(): String {
        return "[PlainText:${content.replace("\n", "\\n")}]"
    }

    override fun toMetaContent(): String {
        return content.replace("<", "&lt;").replace(">", "&gt;")
    }
}

data class Emoji(val id: String, val emojiType: EmojiType = EmojiType.fromValueID(id) ?: EmojiType.NULL) : MessageItem {
    override fun toContent(): String {
        return "/${emojiType.description}"
    }

    override fun toStringType(): String {
        return "[Emoji:$id]"
    }

    override fun toMetaContent(): String {
        return "<emoji:$id>"
    }
}

data class File(val name: String?, val attachment: MessageAttachment) : MessageItem {
    override fun toStringType(): String {
        return "[File:${name?.replace("\n", "\\n")}]"
    }
}

data class Image(val name: String?, val attachment: MessageAttachment) : MessageItem {
    override fun toStringType(): String {
        return "[Image:${name?.replace("\n", "\\n")}]"
    }
}

/**
 * 字段属性作用见 @see [github.zimoyin.net.websocket.bean.User]
 */
data class At(
    val id: String,
    val avatar: String = "",
    val isBot: Boolean = false,
    val name: String = "",
    val unionOpenID: String? = null,
    val unionUserAccount: String? = null,
) : MessageItem {
    override fun toContent(): String {
        return "@$id"
    }

    override fun toStringType(): String {
        return "[At:${id.replace("\n", "\\n")}]"
    }

    override fun toMetaContent(): String {
        return "<@!$id>"
    }
}

data class AtALL(val id: String) : MessageItem {
    override fun toContent(): String {
        return "@$id"
    }

    override fun toStringType(): String {
        return "[At:${id.replace("\n", "\\n")}]"
    }

    override fun toMetaContent(): String {
        return "@$id"
    }
}

data class AtChannelOwnerAll(val id: String) : MessageItem {
    override fun toContent(): String {
        return "@$id"
    }

    override fun toStringType(): String {
        return "[At:$id]"
    }

    override fun toMetaContent(): String {
        return "@$id"
    }
}

data class AtOnlineAll(val id: String) : MessageItem {
    override fun toContent(): String {
        return "@$id"
    }

    override fun toStringType(): String {
        return "[At:$id]"
    }

    override fun toMetaContent(): String {
        return "@$id"
    }
}

data class ReferenceMessage(val id: String) : MessageItem {
    override fun toStringType(): String {
        return "[ReferenceMessage:$id]"
    }
}

data class Ark(val ark: MessageArk, val content: String = JSON.toJsonString(ark)) : MessageItem {
    override fun toContent(): String {
        return content
    }

    override fun toStringType(): String {
        return "[MessageArk:${ark.templateId}]"
    }

    override fun toMetaContent(): String {
        return content
    }
}


data class Embed(val embed: MessageEmbed, val content: String = JSON.toJsonString(embed)) : MessageItem {
    override fun toContent(): String {
        return content
    }

    override fun toStringType(): String {
        return "[MessageEmbed:${embed.title}]"
    }

    override fun toMetaContent(): String {
        return content
    }
}

data class Markdown(val markdown: MessageMarkdown, val content: String = JSON.toJsonString(markdown)) : MessageItem {
    override fun toContent(): String {
        return content
    }

    override fun toStringType(): String {
        return "[MessageMarkdown:${markdown.templateId}]"
    }

    override fun toMetaContent(): String {
        return content
    }
}
