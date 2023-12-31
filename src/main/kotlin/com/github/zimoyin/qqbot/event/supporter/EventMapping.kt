package com.github.zimoyin.qqbot.event.supporter

import com.github.zimoyin.qqbot.event.events.*
import com.github.zimoyin.qqbot.event.events.friend.*
import com.github.zimoyin.qqbot.event.events.group.*
import com.github.zimoyin.qqbot.event.events.platform.*
import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.event.events.bot.BotReadyEvent
import com.github.zimoyin.qqbot.event.events.bot.BotResumedEvent
import com.github.zimoyin.qqbot.event.events.bot.BotStatusEvent
import com.github.zimoyin.qqbot.event.events.channel.ChannelEvent
import com.github.zimoyin.qqbot.event.events.channel.forum.ForumEvent
import com.github.zimoyin.qqbot.event.events.channel.forum.audit.ForumPublishAuditEvent
import com.github.zimoyin.qqbot.event.events.channel.forum.post.ForumPostCreateEvent
import com.github.zimoyin.qqbot.event.events.channel.forum.post.ForumPostDeleteEvent
import com.github.zimoyin.qqbot.event.events.channel.forum.post.ForumPostEvent
import com.github.zimoyin.qqbot.event.events.channel.forum.reply.ForumReplyCreateEvent
import com.github.zimoyin.qqbot.event.events.channel.forum.reply.ForumReplyDeleteEvent
import com.github.zimoyin.qqbot.event.events.channel.forum.reply.ForumReplyEvent
import com.github.zimoyin.qqbot.event.events.channel.forum.thread.ForumThreadCreateEvent
import com.github.zimoyin.qqbot.event.events.channel.forum.thread.ForumThreadDeleteEvent
import com.github.zimoyin.qqbot.event.events.channel.forum.thread.ForumThreadEvent
import com.github.zimoyin.qqbot.event.events.channel.forum.thread.ForumThreadUpdateEvent
import com.github.zimoyin.qqbot.event.events.channel.guild.BotGuildUpdatedEvent
import com.github.zimoyin.qqbot.event.events.channel.guild.BotJoinedGuildEvent
import com.github.zimoyin.qqbot.event.events.channel.guild.BotLeftGuildEvent
import com.github.zimoyin.qqbot.event.events.channel.guild.GuildOperate
import com.github.zimoyin.qqbot.event.events.channel.sub.SubChannelCreateEvent
import com.github.zimoyin.qqbot.event.events.channel.sub.SubChannelDeleteEvent
import com.github.zimoyin.qqbot.event.events.channel.sub.SubChannelEvent
import com.github.zimoyin.qqbot.event.events.channel.sub.SubChannelUpdateEvent
import com.github.zimoyin.qqbot.event.events.message.MessageEvent
import com.github.zimoyin.qqbot.event.events.message.PrivateChannelMessageEvent
import com.github.zimoyin.qqbot.event.events.message.at.AtMessageEvent
import com.github.zimoyin.qqbot.event.events.message.at.ChannelAtMessageEvent
import com.github.zimoyin.qqbot.event.events.message.direct.ChannelPrivateMessageEvent
import com.github.zimoyin.qqbot.event.events.message.direct.PrivateMessageEvent
import com.github.zimoyin.qqbot.event.events.message.direct.UserPrivateMessageEvent
import com.github.zimoyin.qqbot.event.events.paste.MessageAddPasteEvent
import com.github.zimoyin.qqbot.event.events.paste.MessageDeletePasteEvent
import com.github.zimoyin.qqbot.event.events.paste.MessagePasteEvent
import com.github.zimoyin.qqbot.event.events.platform.bot.BotHelloEvent
import com.github.zimoyin.qqbot.event.events.platform.bot.BotOfflineEvent
import com.github.zimoyin.qqbot.event.events.platform.bot.BotOnlineEvent
import com.github.zimoyin.qqbot.event.events.platform.bot.BotReconnectNotificationEvent
import com.github.zimoyin.qqbot.event.events.revoke.ChannelMessageRevokeEvent
import com.github.zimoyin.qqbot.event.events.revoke.ChannelPrivateMessageRevokeEvent
import com.github.zimoyin.qqbot.event.events.revoke.MessageRevokeEvent
import com.github.zimoyin.qqbot.event.handler.NoneEventHandler

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.github.zimoyin.qqbot.net.bean.Payload

/**
 *
 * @author : zimo
 * @date : 2023/12/08
 * 事件元类型与事件处理器映射
 * 事件与事件处理器的映射关系
 */
object EventMapping {
    private val mapping = mutableMapOf<String, MateEventMapping>()
    private val history: HashSet<Class<*>> = HashSet()
    private val logger: Logger by lazy { LoggerFactory.getLogger(EventMapping::class.java) }

    init {
        //添加框架预先实现的元事件。如果不添加的话，父事件无法接收到没有开启监听的子事件的信息
        val success: HashSet<String> = HashSet()
        arrayOf(
            Event::class.java,

            //Message
            MessageEvent::class.java,
            PrivateChannelMessageEvent::class.java, //私域监听信息
            //私信事件
            PrivateMessageEvent::class.java,
            UserPrivateMessageEvent::class.java,
            ChannelPrivateMessageEvent::class.java,
            //AT
            AtMessageEvent::class.java,
            ChannelAtMessageEvent::class.java,

            //Channel 操作信息
            ChannelEvent::class.java,
            // Channel -> Guild
            GuildOperate::class.java,
            BotGuildUpdatedEvent::class.java,
            BotJoinedGuildEvent::class.java,
            BotLeftGuildEvent::class.java,
            // Channel -> Channel
            SubChannelEvent::class.java,
            SubChannelCreateEvent::class.java,
            SubChannelDeleteEvent::class.java,
            SubChannelUpdateEvent::class.java,
            // Channel -> Forum
            ForumEvent::class.java,
            // Channel -> Forum -> Audit
            ForumPublishAuditEvent::class.java,
            // Channel -> Forum -> Post
            ForumPostEvent::class.java,
            ForumPostCreateEvent::class.java,
            ForumPostDeleteEvent::class.java,
            // Channel -> Forum -> Reply
            ForumReplyEvent::class.java,
            ForumReplyCreateEvent::class.java,
            ForumReplyDeleteEvent::class.java,
            // Channel -> Forum -> Thread
            ForumThreadEvent::class.java,
            ForumThreadCreateEvent::class.java,
            ForumThreadDeleteEvent::class.java,
            ForumThreadUpdateEvent::class.java,


            //Group
            GroupEvent::class.java,
            GroupMemberUpdateEvent::class.java,
            GroupBotOperationEvent::class.java,
            AddGroupEvent::class.java,
            ExitGroupEvent::class.java,
            CloseGroupBotEvent::class.java,
            OpenGroupBotEvent::class.java,

            //Friend
            FriendEvent::class.java,
            FriendUpdateEvent::class.java,
            AddFriendEvent::class.java,
            DeleteFriendEvent::class.java,
            FriendBotOperationEvent::class.java,
            OpenFriendBotEvent::class.java,
            CloseFriendBotEvent::class.java,

            //MESSAGE_REACTION
            MessagePasteEvent::class.java,
            MessageDeletePasteEvent::class.java,
            MessageAddPasteEvent::class.java,

            //MESSAGE_DELETE
            MessageRevokeEvent::class.java,
            ChannelPrivateMessageRevokeEvent::class.java,
            ChannelMessageRevokeEvent::class.java,

            //Audit
            MessageAuditEvent::class.java,
            MessageStartAuditEvent::class.java,
            MessageAuditPassEvent::class.java,
            MessageAuditRejectEvent::class.java,

            //PlatformEvent
            PlatformEvent::class.java,
            MessageSendPreEvent::class.java,
            MessageSendEvent::class.java,
            ChannelMessageSendPreEvent::class.java,
            ChannelMessageSendEvent::class.java,
            MessageSendInterceptEvent::class.java,

            //BOT
            BotStatusEvent::class.java,
            BotHelloEvent::class.java,
            BotReconnectNotificationEvent::class.java,
            BotOnlineEvent::class.java,
            BotOfflineEvent::class.java,
            BotResumedEvent::class.java,
            BotReadyEvent::class.java,

            //TODO
        ).forEach { cls ->
            kotlin.runCatching { initAdd(cls) }
                .onFailure {
                    logger.error("EventMapping add error: class $cls", it)
                }
                .onSuccess {
                    success.add(cls.simpleName)
                }
        }
        logger.debug("EventMapping 初始化成功: {}", success)
    }

    fun add(str: String, mate: MateEventMapping) {
        mapping[str] = mate
    }

    fun <T : Event> add(cls: Class<T>) {
        if (history.contains(cls)) return
        runCatching {
            val initAdd = initAdd(cls)
            logger.debug("EventMapping add mapping: {} ", initAdd)
        }.onFailure {
            logger.error("EventMapping add error: class $cls", it)
        }
    }

    fun <T : Event> initAdd(cls: Class<T>): MateEventMapping? {
        if (history.contains(cls)) return null
        val metaTypeAnnotation = cls.getAnnotation(EventAnnotation.EventMetaType::class.java)
            ?: throw NullPointerException("There is no EventAnnotation.EventMetaType annotation in $cls")
        var eventHandlerAnnotation = cls.getAnnotation(EventAnnotation.EventHandler::class.java)

        if (eventHandlerAnnotation == null) logger.warn("该事件上未能声明处理器，将使用父处理器，同时你需要承担使用父处理器带来的事件广播只从该处理器事件进行广播的问题: [$cls]")
        while (eventHandlerAnnotation == null) {
            val superclass = cls.superclass ?: break
            eventHandlerAnnotation = superclass.getAnnotation(EventAnnotation.EventHandler::class.java)
        }
        eventHandlerAnnotation
            ?: throw NullPointerException("There is no EventAnnotation.EventHandler annotation in $cls")

        val metadataType = metaTypeAnnotation.metadataType
        val eventHandler = eventHandlerAnnotation.eventHandler

        val returnType = eventHandler.java.getMethod("handle", Payload::class.java).returnType
        if ((returnType!= cls && eventHandler != NoneEventHandler::class) && !eventHandlerAnnotation.ignore) {
            logger.warn("EventAnnotation.EventHandler 注解中注册的处理器的返回值类型并不是该事件的类型 [$cls] 这将导致无法正确的广播事件，意味着该事件无法被正常广播，而是广播到该处理器处理返回的事件中.")
        }

        mapping[metadataType] = MateEventMapping(
            eventType = metadataType,
            eventCls = cls,
            eventHandler = eventHandler.java
        )
        history.add(cls)
        return mapping[metadataType]
    }

    fun get(str: String): MateEventMapping? = mapping[str]

    @Deprecated("请不要考虑从映射表中清理映射")
    fun remove(str: String): MateEventMapping? = mapping.remove(str)?.apply {
        history.remove(this.eventCls)
    }

    fun clear() {
        mapping.clear()
        history.clear()
    }
}
