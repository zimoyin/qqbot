package io.github.zimoyin.qqbot.bot

import io.github.zimoyin.qqbot.net.Token
import java.io.Serializable

/**
 *
 * @author : zimo
 * @date : 2023/12/12
 *
 * 机器人信息，该信息用于网络传输的
 */
data class BotInfo(
    val token: Token,
    /**
     * Bot 的QQ号
     */
    val id: String,
    /**
     * Bot 的昵称
     */
    val nick: String,
    /**
     * 机器人头像地址
     */
    val avatar: String,
    /**
     * 特殊关联应用的 openid
     * 需要特殊申请并配置后才会返回
     */
    val unionOpenid: String?,

    /**
     * 机器人关联的互联应用的用户信息
     * 与 union_openid 关联的应用是同一个
     */
    val unionUserAccount: String?,
) : Serializable {
    companion object {
        /**
         * 该方法会自动获取 bot 的信息，所以不能在非主要程序中使用，否则无法获取到 bot
         */
        @JvmStatic
        fun create(appid: String): BotInfo {
            val bot = Bot.getBot(appid)
            return BotInfo(
                token = bot.config.token,
                id = bot.id,
                nick = bot.nick,
                avatar = bot.avatar,
                unionOpenid = bot.unionOpenid,
                unionUserAccount = bot.unionUserAccount,
            )
        }

        @JvmStatic
        fun create(bot: Bot): BotInfo = BotInfo(
            token = bot.config.token,
            id = bot.id,
            nick = bot.nick,
            avatar = bot.avatar,
            unionOpenid = bot.unionOpenid,
            unionUserAccount = bot.unionUserAccount,
        )

        @JvmStatic
        fun emptyBotInfo(): BotInfo = BotInfo(
            token = Token("emptyBotInfo"),
            id = "emptyBotInfo",
            nick = "emptyBotInfo",
            avatar = "emptyBotInfo",
            unionOpenid = "emptyBotInfo",
            unionUserAccount = "emptyBotInfo",
        )
    }
}
