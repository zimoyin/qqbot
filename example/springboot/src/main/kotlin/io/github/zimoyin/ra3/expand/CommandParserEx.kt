package io.github.zimoyin.ra3.expand

import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import io.github.zimoyin.ra3.service.CommandParser

/**
 *
 * @author : zimo
 * @date : 2025/01/05
 */
fun MessageEvent.getCommand(): CommandParser.CommandParserBean {
    return CommandParser.parse(this)
}