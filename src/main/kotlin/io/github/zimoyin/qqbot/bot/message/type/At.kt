package io.github.zimoyin.qqbot.bot.message.type

/**
 * 字段属性作用见 @see [github.zimoyin.net.websocket.bean.User]
 */
class At(
    val id: String,
    val avatar: String = "",
    val isBot: Boolean = false,
    val name: String = "",
    val unionOpenID: String? = null,
    val unionUserAccount: String? = null,
) : MessageItem {
    constructor(id: String) : this(id, "", false, "", null, null)

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

class AtALL(val id: String) : MessageItem {
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

class AtChannelOwnerAll(val id: String) : MessageItem {
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

class AtOnlineAll(val id: String) : MessageItem {
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
