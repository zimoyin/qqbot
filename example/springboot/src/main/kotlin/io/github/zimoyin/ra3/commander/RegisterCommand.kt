package io.github.zimoyin.ra3.commander

import io.github.zimoyin.ra3.annotations.Commander
import io.github.zimoyin.qqbot.event.events.message.MessageEvent

/**
 *
 * @author : zimo
 * @date : 2025/01/03
 */
@Commander("注册", executeMethod = "register")
class RegisterCommand {
    /**
     * 注册命令
     */
    fun register(event:MessageEvent){
        event.reply("注册成功")
    }
}