package io.github.zimoyin.qqbot.test

import io.github.zimoyin.qqbot.LocalLogger
import io.github.zimoyin.qqbot.SystemLogger
import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Contact
import io.github.zimoyin.qqbot.bot.contact.Sender
import io.github.zimoyin.qqbot.bot.contact.User
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import io.github.zimoyin.qqbot.event.events.message.PrivateChannelMessageEvent
import io.github.zimoyin.qqbot.event.events.platform.bot.BotOnlineEvent
import io.github.zimoyin.qqbot.event.handler.message.MessageHandler
import io.github.zimoyin.qqbot.event.supporter.EventMapping
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus.bus
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus.debugLogger
import io.github.zimoyin.qqbot.net.Intents
import io.github.zimoyin.qqbot.net.Token
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean
import io.github.zimoyin.qqbot.utils.ex.md5
import io.github.zimoyin.qqbot.utils.io
import io.github.zimoyin.qqbot.utils.vertxWorker
import io.vertx.core.Future
import io.vertx.core.Vertx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import openDebug
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.util.*
import kotlin.random.Random

/**
 *
 * @author : zimo
 * @date : 2024/12/26
 */
fun main() {
   listOf(1,2,3,4).forEach {
       println(it)
       return@forEach
   }
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

