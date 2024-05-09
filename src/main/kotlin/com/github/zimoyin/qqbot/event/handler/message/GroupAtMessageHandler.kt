package com.github.zimoyin.qqbot.event.handler.message

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.zimoyin.qqbot.bot.BotInfo
import com.github.zimoyin.qqbot.bot.contact.GroupImpl
import com.github.zimoyin.qqbot.bot.contact.Sender
import com.github.zimoyin.qqbot.bot.message.MessageChain
import com.github.zimoyin.qqbot.event.events.message.at.GroupAtMessageEvent
import com.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import com.github.zimoyin.qqbot.net.bean.message.Message
import com.github.zimoyin.qqbot.net.bean.Payload
import com.github.zimoyin.qqbot.utils.JSON
import com.github.zimoyin.qqbot.utils.ex.toJsonObject
import org.slf4j.LoggerFactory

/**
 *
 * @author : zimo
 * @date : 2023/12/11
 * 频道私信事件
 */
class GroupAtMessageHandler : AbsEventHandler<GroupAtMessageEvent>() {
    private val logger = LoggerFactory.getLogger(GroupAtMessageHandler::class.java)

    override fun handle(payload: Payload): GroupAtMessageEvent {
        // Group 不会下发用户的名称的，所以给用户名称设置成ID，防止错误
        val jsonString = payload.eventContent.toString()
        val mapper = ObjectMapper()
        val jsonNode = mapper.readTree(jsonString)

        var jsonObject = jsonNode.toJsonObject()
//        jsonObject = jsonObject.apply {
//            val value = getJsonObject("d")?.apply {
//                val value = getJsonObject("author")?.apply {
//                    if (getString("username") != null) logger.error("Group At Message Event: Group 下发了 username，请联系开发者修正该部分代码")
//                    else put("username", getString("id"))
//                }
//                put("author", value)
//            }
//            put("d", value)
//        }
        jsonObject = jsonObject.apply {
            val value = getJsonObject("author")?.apply {
                if (getString("username") != null) logger.error("Group At Message Event: Group 下发了 username，请联系开发者修正该部分代码")
                else put("username", getString("id"))
            }
            put("author", value)
        }

        //        val message = JSON.toObject<Message>(payload.eventContent.toString()) // 如果Group 下发了 username 就使用该代码，并把上面代码删除
        val message = JSON.toObject<Message>(jsonObject)
        val msgID = message.msgID!!
        val messageChain = MessageChain.convert(message)
        val botInfo = BotInfo.create(payload.appID!!)
        val sender = Sender.convert(botInfo, message)
        val group = GroupImpl.convert(botInfo, message)

        return GroupAtMessageEvent(
            metadata = payload.metadata,
            msgID = msgID,
            windows = group,
            messageChain = messageChain,
            sender = sender,
            botInfo = botInfo,
            groupID = group.id,
            timestamp = message.timestamp!!,
            opMemberOpenid = sender.id
        )
    }
}
