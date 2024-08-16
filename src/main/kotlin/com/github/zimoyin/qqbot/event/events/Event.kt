package com.github.zimoyin.qqbot.event.events

import com.github.zimoyin.qqbot.annotation.EventAnnotation
import com.github.zimoyin.qqbot.bot.Bot
import com.github.zimoyin.qqbot.bot.BotInfo
import java.io.Serializable

/**
 *
 * @author : zimo
 * @date : 2023/12/06/17:42
 */
@EventAnnotation.EventMetaType("Not_MetaType_Event")
@EventAnnotation.EventHandler
interface Event : Serializable {
    val metadata: String
    val metadataType: String
    val botInfo: BotInfo
    val eventID: String
        get() = ""

    val localTimestamp: Long
        get() = System.currentTimeMillis()

    /**
     * 获取机器人实例，但是请注意机器人实例只是在当前的程序中获取，严禁在其他程序中获取。
     * 也就是禁止在集群中传输Bot实例,原因在于Bot 中会存储 vertx 实例，以及一些可能会导致意外的上下文信息
     */
    fun getBot(): Bot {
        return Bot.get(botInfo.token.appID)
            ?: throw NullPointerException("Bot is null. Every bot is private, you should not read it in other programs")
    }
}
