package io.github.zimoyin.qqbot.event.events.group.operation

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Contact
import io.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean
import io.vertx.core.Future
import java.util.*

/**
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * 群管理员主动在机器人资料页操作开启通知
 */
@EventAnnotation.EventMetaType("GROUP_MSG_RECEIVE")
@EventAnnotation.EventHandler(io.github.zimoyin.qqbot.event.handler.group.OpenGroupBotHandler::class)
data class OpenGroupBotEvent(
    override val metadata: String,
    override val metadataType: String = "GROUP_MSG_RECEIVE",
    override val botInfo: BotInfo,
    override val groupID: String,
    override val timestamp: Date,
    override val opMemberOpenid: String,
    /**
     * 会话窗口：通常用于信息来源。来自于哪个组，比如群组，私信等
     */
    val windows: Contact,
    override val eventID: String
) : GroupBotOperationEvent{
    /**
     * 被动回复信息
     * 注意无法通过事件发送主动信息，请查询 Content.send 方法
     */
    @Deprecated("文档显示支持了该回复，但是服务器不支持")
    fun reply(message: String): Future<SendMessageResultBean> {
        return windows.send(MessageChainBuilder().appendEventId(eventID).append(message).build())
    }

    /**
     * 被动回复信息
     * 注意无法通过事件发送主动信息，请查询 Content.send 方法
     * 注: 对于非文本等形式的消息，可能会受限于主动信息推送
     */
    @Deprecated("文档显示支持了该回复，但是服务器不支持")
    fun reply(messageBuild: MessageChainBuilder): Future<SendMessageResultBean> {
        messageBuild.appendEventId(eventID)
        return windows.send(messageBuild.build())
    }
}
