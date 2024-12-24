package example.java.create_bot;

import io.github.zimoyin.qqbot.ConfigKt;
import io.github.zimoyin.qqbot.bot.Bot;
import io.github.zimoyin.qqbot.event.events.Event;
import io.github.zimoyin.qqbot.event.events.message.MessageEvent;
import io.github.zimoyin.qqbot.net.Intents;
import io.github.zimoyin.qqbot.net.Token;
import io.github.zimoyin.qqbot.net.http.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : zimo
 * @date : 2024/05/09
 */
public class Main1 {
    private static final Logger log = LoggerFactory.getLogger(Main1.class);
    public static String AppID = "xxx";
    public static String Tokens = "xxx";
    public static String Secret = "xxx";

    public static void main(String[] args) {
        // 设置沙盒环境
        TencentOpenApiHttpClient.setSandBox(true);

        // 创建Token，并使用鉴权方式 1
        Token token = Token.create(AppID, Tokens, Secret).version(1);

        /*
            订阅事件的两种方式；
            如果想要两个事件融合可以使用 and 方法
        */
        // 订阅一个事件和一组事件。定于私域和群组事件
        int intent = Intents.create(Intents.GROUP_INTENTS.getCode(), Intents.Presets.PRIVATE_INTENTS.getCode());
        int intent2 = Intents.create(Intents.GROUP_INTENTS, Intents.OPEN_FORUMS_EVENT);
        Bot bot_1 = Bot.createBot(token, intent);

        // 创建 Bot 并订阅私域事件
        Bot bot = Bot.createBot(token, Intents.Presets.PRIVATE_INTENTS);

        // 事件注册
        // 事件注册可以放到登录后注册，但是会错过部分登录事件
        bot.onEvent(MessageEvent.class, event -> {
            System.out.println("接收到信息: "+event.getMessageChain());
            event.reply(event.getMessageChain()); // 发送信息
        });

        // 登录
        bot.login().onSuccess(ws -> {
            log.info("登录成功");
            new Thread(() -> {
                try {
                    Thread.sleep(9999);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                //关闭机器人
                ws.close().onSuccess(r -> {
                    log.info("关闭机器人成功");
                }).onFailure(throwable -> {
                    log.error("关闭机器人失败", throwable);
                });
                // 关闭应用
                ConfigKt.getGLOBAL_VERTX_INSTANCE().close().onSuccess(r -> {
                    log.info("关闭应用成功");
                }).onFailure(throwable -> {
                    log.error("关闭应用失败", throwable);
                });
            }).start();

        }).onFailure(throwable -> {
            log.error("登录失败", throwable);
        });
    }
}
