package example.java.create_bot;

import io.github.zimoyin.qqbot.bot.Bot;
import io.github.zimoyin.qqbot.bot.BotConfigBuilder;
import io.github.zimoyin.qqbot.event.events.message.MessageEvent;
import io.github.zimoyin.qqbot.net.Intents;
import io.github.zimoyin.qqbot.net.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : zimo
 * @date : 2024/05/09
 */
public class Main2 {
    private static final Logger log = LoggerFactory.getLogger(Main2.class);
    public static String AppID = "xxx";
    public static String Tokens = "xxx";
    public static String Secret = "xxx";

    public static void main(String[] args) {
        // 创建Token，并使用鉴权方式 1
        Token token = Token.create(AppID, Tokens, Secret).version(1);

        BotConfigBuilder config = new BotConfigBuilder()
//            .setIntents(Intents.GROUP_INTENTS,Intents.FORUMS_EVENT)
//            .setIntents(Intents.Presets.PRIVATE_INTENTS.getCode())
            .setIntents(Intents.Presets.PRIVATE_INTENTS)
            .setToken(token);

        // 创建 Bot 并订阅私域事件
        Bot bot = Bot.createBot(config);

        // 登录
        bot.login();
    }
}
