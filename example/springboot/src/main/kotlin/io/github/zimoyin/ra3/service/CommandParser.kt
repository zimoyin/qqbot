package io.github.zimoyin.ra3.service

import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import org.springframework.stereotype.Service

/**
 *
 * @author : zimo
 * @date : 2025/01/04
 */
@Service
class CommandParser {

    companion object {
        fun parse(event: MessageEvent): CommandParserBean {
            return parse(event.messageChain.content()).apply {
                senderID = event.sender.id
            }
        }

        fun parse(command: String): CommandParserBean {
            val list = command.split("\\s".toRegex()).filter {
                it.isNotEmpty()
            }
            val params = if (list.size >= 2) list.subList(1, list.size) else emptyList()
            return CommandParserBean(
                command = list.first(),
                params = params,
                paramContent = params.joinToString(" ")
            )
        }
    }

    data class CommandParserBean(
        val command: String,
        val params: List<String>,
        val paramContent: String,
    ) {
        var senderID = ""
        val first: String by lazy {
            params.firstOrNull() ?: ""
        }

        val second: String by lazy {
            params.getOrNull(1) ?: ""
        }

        val third: String by lazy {
            params.getOrNull(2) ?: ""
        }

        val last: String by lazy {
            params.lastOrNull() ?: ""
        }

        operator fun get(index: Int): String {
            return params.getOrNull(index) ?: ""
        }
    }
}