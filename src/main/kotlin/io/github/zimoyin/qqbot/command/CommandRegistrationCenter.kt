package io.github.zimoyin.qqbot.command

import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.bot.message.type.At
import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import io.github.zimoyin.qqbot.exception.CommandException
import io.github.zimoyin.qqbot.exception.CommandHandlerException
import io.github.zimoyin.qqbot.exception.CommandNotFoundException
import java.util.function.Consumer

/**
 *
 * @author : zimo
 * @date : 2024/05/10
 * 简易的命令注册中心
 */
object SimpleCommandRegistrationCenter {
    private val commandMap = HashSet<SimpleCommandObject>()

    /**
     * 注册命令
     */
    fun register(command: SimpleCommandObject): Boolean = commandMap.add(command)

    /**
     * 注销命令
     */
    fun unregister(command: SimpleCommandObject): Boolean = commandMap.remove(command)

    /**
     * 注册命令
     */
    @Deprecated(
        "请使用 register(command: SimpleCommandObject)", ReplaceWith(
            "register(SimpleCommandObject(command){})",
            "io.github.zimoyin.qqbot.command.SimpleCommandRegistrationCenter.register",
            "io.github.zimoyin.qqbot.command.SimpleCommandObject"
        )
    )
    fun register(command: String): Boolean = register(SimpleCommandObject(command))

    /**
     * 注销命令
     */
    fun unregister(command: String): Boolean = unregister(SimpleCommandObject(command))

    /**
     * 注册命令
     */
    fun register(command: String, handle: Consumer<SimpleCommandInfo?>): Boolean {
        return commandMap.add(SimpleCommandObject(command) { handle.accept(it) })
    }

    /**
     * 获取命令列表
     */
    fun getCommandList(): Set<SimpleCommandObject> = commandMap.toSet()

    /**
     * 获取命令,如果没有则返回 null
     * @param commandLine 一段完整的命令，包含命令主语与参数等
     * @return CommandObject?
     */
    fun getCommand(commandLine: String): SimpleCommandObject? {
        // 匹配相似的命令主语
        val filter = commandMap.filter {
            commandLine.trim().split("\\s".toRegex()).first().startsWith(it.commandSubject)
        }
        if (filter.size == 1) return filter.first()
        if (filter.isEmpty()) return null
        // 匹配相同主语
        val first = filter.firstOrNull() {
            commandLine.trim().split("\\s".toRegex()).first() == it.commandSubject
        }
        if (first != null) return first
        return filter.firstOrNull()
    }


    /**
     * 执行命令
     */
    @Throws(CommandHandlerException::class, CommandNotFoundException::class, CommandException::class)
    fun execute(event: MessageEvent) {
        val chain = event.messageChain
        val content = getContent(chain)
        val cs = content.trim().split("\\s".toRegex()).first()
        val command = getCommand(content) ?: throw CommandNotFoundException("The command was not found: $cs")
        val param = content.trim().substring(command.commandSubject.length)
        try {
            command.handle(SimpleCommandInfo(param, event))
        } catch (e: Exception) {
            throw CommandHandlerException(
                msg = "An exception occurred when the command handler processed the command: ${command.commandSubject}",
                throwable = e
            )
        }
    }

    /**
     * 执行命令，发生错误就返回null，执行成功就返回 true
     * @return Boolean 命令执行成功或者失败
     */
    fun executeOrBoolean(event: MessageEvent): Boolean {
        val chain = event.messageChain
        val content = getContent(chain)
        val command = getCommand(content) ?: return false
        val param = content.trim().substring(command.commandSubject.length)
        try {
            command.handle(SimpleCommandInfo(param, event))
        } catch (e: Exception) {
            return false
        }
        return true
    }

    private fun getContent(chain: MessageChain): String {
        val sb = StringBuilder()
        chain.forEach {
            if (it.javaClass == At::class.java) return@forEach
            sb.append(it.toContent())
        }
        return sb.toString()
    }
}
