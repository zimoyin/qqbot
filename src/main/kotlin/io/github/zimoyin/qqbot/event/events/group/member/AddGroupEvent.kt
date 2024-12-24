package io.github.zimoyin.qqbot.event.events.group.member

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Contact
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import io.github.zimoyin.qqbot.event.handler.group.AddGroupHandler
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean
import io.vertx.core.Future
import java.util.*

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 机器人加入群聊
 */

@EventAnnotation.EventMetaType("GROUP_ADD_ROBOT")
@EventAnnotation.EventHandler(AddGroupHandler::class)
data class AddGroupEvent(
    override val metadata: String,
    override val metadataType: String = "GROUP_ADD_ROBOT",
    override val botInfo: BotInfo,
    override val groupID :String,
    override val timestamp :Date,
    override val opMemberOpenid :String,
    /**
     * 会话窗口：通常用于信息来源。来自于哪个组，比如群组，私信等
     */
    val windows: Contact,
    override val eventID: String = ""
): GroupMemberUpdateEvent{
    /**
     * 被动回复信息
     * 注意无法通过事件发送主动信息，请查询 Content.send 方法
     */
    fun reply(message: String): Future<SendMessageResultBean> {
        return windows.send(MessageChainBuilder().appendEventId(eventID).append(message).build())
    }

    /**
     * 被动回复信息
     * 注意无法通过事件发送主动信息，请查询 Content.send 方法
     * 注: 对于非文本等形式的消息，可能会受限于主动信息推送
     */
    fun reply(messageBuild: MessageChainBuilder): Future<SendMessageResultBean> {
        messageBuild.appendEventId(eventID)
        return windows.send(messageBuild.build())
    }


}
