package io.github.zimoyin.qqbot.test

import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.message.type.AudioMessage
import io.github.zimoyin.qqbot.bot.message.type.AudioMessage.Companion.create
import io.github.zimoyin.qqbot.net.Intents
import java.io.File

/**
 *
 * @author : zimo
 * @date : 2024/12/26
 */
@OptIn(UntestedApi::class)
fun main() {
    AudioMessage.create("http://./out/output.ogg")
}

// 转换
fun transformTo(eventType: String): String {
    return when (eventType) {
        "GUILD_MESSAGES", "MESSAGE_CREATE", "MESSAGE_DELETE" -> Intents.GUILD_MESSAGES.name
        "PUBLIC_GUILD_MESSAGES", "AT_MESSAGE_CREATE", "PUBLIC_MESSAGE_DELETE" -> Intents.PUBLIC_GUILD_MESSAGES.name
        "AUDIO_ACTION", "AUDIO_START", "AUDIO_FINISH", "AUDIO_ON_MIC", "AUDIO_OFF_MIC" -> Intents.AUDIO_ACTION.name
        "FORUMS_EVENT", "FORUM_THREAD_CREATE", "FORUM_THREAD_UPDATE", "FORUM_THREAD_DELETE", "FORUM_POST_CREATE", "FORUM_POST_DELETE", "FORUM_REPLY_CREATE", "FORUM_REPLY_DELETE", "FORUM_PUBLISH_AUDIT_RESULT" -> Intents.FORUMS_EVENT.name
        "MESSAGE_AUDIT", "MESSAGE_AUDIT_PASS", "MESSAGE_AUDIT_REJECT" -> Intents.MESSAGE_AUDIT.name
        "INTERACTION", "INTERACTION_CREATE" -> Intents.INTERACTION.name
        "GROUP_INTENTS", "GROUP_AND_C2C_EVENT" -> Intents.GROUP_INTENTS.name
        "AUDIO_OR_LIVE_CHANNEL_MEMBER", "AUDIO_OR_LIVE_CHANNEL_MEMBER_ENTER", "AUDIO_OR_LIVE_CHANNEL_MEMBER_EXIT" -> Intents.AUDIO_OR_LIVE_CHANNEL_MEMBER.name
        "OPEN_FORUMS_EVENT", "OPEN_FORUM_THREAD_CREATE", "OPEN_FORUM_THREAD_UPDATE", "OPEN_FORUM_THREAD_DELETE", "OPEN_FORUM_POST_CREATE", "OPEN_FORUM_POST_DELETE", "OPEN_FORUM_REPLY_CREATE", "OPEN_FORUM_REPLY_DELETE" -> Intents.OPEN_FORUMS_EVENT.name
        "DIRECT_MESSAGE", "DIRECT_MESSAGE_CREATE", "DIRECT_MESSAGE_DELETE" -> Intents.DIRECT_MESSAGE.name
        "GUILD_MESSAGE_REACTIONS", "MESSAGE_REACTION_ADD", "MESSAGE_REACTION_REMOVE" -> Intents.GUILD_MESSAGE_REACTIONS.name
        "GUILD_MEMBERS", "GUILD_MEMBER_ADD", "GUILD_MEMBER_UPDATE", "GUILD_MEMBER_REMOVE" -> Intents.GUILD_MEMBERS.name
        "GUILDS", "GUILD_CREATE", "GUILD_UPDATE", "GUILD_DELETE" -> Intents.GUILDS.name
        else -> eventType
    }
}

