package io.github.zimoyin.ra3.commander

import io.github.zimoyin.ra3.annotations.ICommand
import io.github.zimoyin.ra3.annotations.Commander
import io.github.zimoyin.ra3.annotations.NotFundCommand
import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import org.springframework.stereotype.Component

/**
 *
 * @author : zimo
 * @date : 2025/01/03
 */
@Component
// 1. 通过注解的方式注册命令
@Commander(name = "hello2", executeMethod = "test")
// 2. 通过实现类方式注册命令
class HelloCommand : ICommand<MessageEvent> {
    override fun execute(event: MessageEvent) {
        event.reply("Hello HelloCommand.execute")
    }

    fun test(event: MessageEvent){
        event.reply("Hello HelloCommand.test")
    }

    // 3. 通过注解的方式注册方法级别的命令
    @Commander(name = "hello3")
    fun hello3(event: MessageEvent) {
        event.reply("Hello HelloCommand.hello3")
    }


    // 4. 处理找不到命令一次
    @NotFundCommand()
    fun notFundCommand(event: MessageEvent) {
        event.reply("找不到命令")
    }
}