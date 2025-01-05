package io.github.zimoyin.ra3.service

import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

/**
 *
 * @author : zimo
 * @date : 2025/01/04
 */
@Service
class CommandParser {

    fun parse(event: MessageEvent): CommandParserBean {
        return parse(event.messageChain.content())
    }

    fun parse(command: String): CommandParserBean {
        val list = command.split("\\s".toRegex()).filter {
            it.isNotEmpty()
        }
        val params = if (list.size >= 2) list.subList(1, list.size) else emptyList()
        return CommandParserBean(
            command = list.first(),
            params = params,
            param = params.joinToString(" ")
        )
    }

    data class CommandParserBean(
        val command: String,
        val params: List<String>,
        val param: String,
    )
}