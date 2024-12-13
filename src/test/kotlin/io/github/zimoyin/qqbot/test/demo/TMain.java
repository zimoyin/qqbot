package io.github.zimoyin.qqbot.test.demo;

import com.github.zimoyin.qqbot.Config;
import com.github.zimoyin.qqbot.bot.Bot;
import com.github.zimoyin.qqbot.bot.message.MessageChain;
import com.github.zimoyin.qqbot.bot.message.MessageChainBuilder;
import com.github.zimoyin.qqbot.bot.message.type.KeyboardMessage;
import com.github.zimoyin.qqbot.bot.message.type.MarkdownMessage;
import com.github.zimoyin.qqbot.event.events.Event;
import com.github.zimoyin.qqbot.event.events.message.MessageEvent;
import com.github.zimoyin.qqbot.event.supporter.GlobalEventBus;
import com.github.zimoyin.qqbot.net.Intents;
import com.github.zimoyin.qqbot.net.Token;
import com.github.zimoyin.qqbot.net.bean.message.MessageMarkdown;
import com.github.zimoyin.qqbot.net.http.api.API;
import com.github.zimoyin.qqbot.net.http.api.TencentOpenApiHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : zimo
 * @date : 2024/11/26
 */
public class TMain {
    public static void run(Token token) {

        // 性能优化：关闭SSL 验证
//        DefaultHttpClient.INSTANCE.setHeadSSL(false);

        long start = System.currentTimeMillis();
        long start2 = start;
        String url = "http://ts1.cn.mm.bing.net/th/id/R-C.23034dbcaded6ab4169b9514f76f51b5?rik=mSGADwV9o/teUA&riu=http://pic.bizhi360.com/bbpic/40/9640_1.jpg&ehk=RYei4n5qyNCPVysJmE2a3WhxSOXqGQMGJcvWBmFyfdg=&risl=&pid=ImgRaw&r=0";

        Logger logger = LoggerFactory.getLogger("Main");

        //全局事件监听
        GlobalEventBus.INSTANCE.onEvent(Event.class,true, event -> {
            logger.info("收到事件：{}", event);
        });

        System.out.println("Vertx 与组件初始化耗时: "+(System.currentTimeMillis()-start));
        start2 = System.currentTimeMillis();

        TencentOpenApiHttpClient.setSandBox(false);

        Bot bot = Bot.createBot(config->{
            config.setToken(token);
            config.setIntents(Intents.Presets.PRIVATE_GROUP_INTENTS);
        });

        System.out.println("Bot 创建耗时: "+(System.currentTimeMillis()-start));
        System.out.println("Bot 创建耗时: "+(System.currentTimeMillis()-start2));
        start2 = System.currentTimeMillis();

//        bot.getConfig().setRetry(99);
//        bot.getContext().set("SESSION_ID", "d5141070-a591-47fa-b334-8ed1eff92ec6");
        API.setDebug(true);
        bot.getContext().set("internal.isAbnormalCardiacArrest", true);
        bot.getContext().set("internal.headerCycle", 5*1000);
        bot.getContext().set("PAYLOAD_CMD_HANDLER_DEBUG_LOG", false);
        bot.getContext().set("PAYLOAD_CMD_HANDLER_DEBUG_MATA_DATA_LOG", false);
        bot.getContext().set("PAYLOAD_CMD_HANDLER_DEBUG_HEART_BEAT", false);

        bot.onEvent(MessageEvent.class, event ->{
//            MessageChain chain = new MessageChainBuilder().append(ImageMessage.create(url)).append("你好").build();
//            event.quote(chain).onFailure(e->{
//                logger.error("发送失败",e);
//            });

//            MessageChain messageChain = MessageMarkdown.create("102077167_1706091638")
//                .appendParam("date", "123")
//                .appendParam("rw", event.getMessageChain().content())
//                .build()
//                .toMessageChain();


            MarkdownMessage mb = MessageMarkdown.create("102077167_1706091638")
                .appendParam("date", "123")
                .appendParam("rw", event.getMessageChain().content())
                .build();
            KeyboardMessage keyboardMessage = KeyboardMessage.createByID("102077167_1733995104");
            MessageChain messageChain = new MessageChainBuilder()
                .append(mb)
                .append(keyboardMessage)
                .build();

            System.out.println(event.getMessageChain().content());
            event.reply(messageChain).onFailure(e -> {
                logger.error("发送失败", e);
            }).onSuccess(r -> {
                logger.info("发送成功: {}", r);
            });
        });


        long finalStart = start2;
        long finalStart1 = start2;
        bot.login().onSuccess(ws->{
            logger.info("登录成功");
            System.out.println("OK");
            System.out.println("启动耗时: "+(System.currentTimeMillis()-start));
            System.out.println("启动耗时: "+(System.currentTimeMillis()- finalStart));
            System.exit(1);
        }).onFailure(e->{
            logger.error("登录失败",e);
            bot.close();
            Config.getGLOBAL_VERTX_INSTANCE().close();
            System.out.println("启动耗时: "+(System.currentTimeMillis()-start));
            System.out.println("启动耗时: "+(System.currentTimeMillis()- finalStart1));
            System.exit(1);
        });
    }
}
