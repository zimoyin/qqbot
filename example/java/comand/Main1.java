package example.java.comand;

import com.github.zimoyin.qqbot.bot.Bot;
import com.github.zimoyin.qqbot.bot.BotConfigBuilder;
import com.github.zimoyin.qqbot.command.SimpleCommandRegistrationCenter;
import com.github.zimoyin.qqbot.event.events.message.MessageEvent;
import com.github.zimoyin.qqbot.exception.CommandException;
import com.github.zimoyin.qqbot.exception.CommandHandlerException;
import com.github.zimoyin.qqbot.exception.CommandNotFoundException;
import com.github.zimoyin.qqbot.net.Intents;
import com.github.zimoyin.qqbot.net.Token;
import com.github.zimoyin.qqbot.net.http.DefaultHttpClient;


/**
 * @author : zimo
 * @date : 2024/05/10
 */
public class Main1 {
    public static String AppID = "xxx";
    public static String Tokens = "xxxx";
    public static String Secret = "xxxxx";
    public static void main(String[] args) {
        // 创建Token，并使用鉴权方式 1
        Token token = Token.create(AppID, Tokens, Secret).version(1);

        BotConfigBuilder config = new BotConfigBuilder()
            .setIntents(Intents.Presets.PRIVATE_GROUP_INTENTS)
            .setToken(token);

        // 设置沙盒环境
        DefaultHttpClient.INSTANCE.setSandBox(true);

        // 创建命令注册中心
        SimpleCommandRegistrationCenter registrationCenter = SimpleCommandRegistrationCenter.INSTANCE;
        registrationCenter.register("/测试",info->{
            MessageEvent event = info.event;
            event.reply("测试程序正常");
        });

        // 创建 Bot 并订阅私域事件
        Bot bot = Bot.createBot(config);
        bot.onEvent(MessageEvent.class, event -> System.out.println(event.getMessageChain()));
        // 让该事件走命令注册中心来执行命令
//        bot.onEvent(MessageEvent.class, registrationCenter::executeOrBoolean);
        bot.onEvent(MessageEvent.class, messageEvent ->{
            try {
                // 执行命令，并处理执行失败的异常
                registrationCenter.execute(messageEvent);
            } catch (CommandNotFoundException | CommandHandlerException e) {
                System.err.println(e.getMessage());
            } catch (CommandException e){
                e.printStackTrace();
            }
        });

        // 登录
        bot.login();
    }
}
