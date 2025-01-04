package io.github.zimoyin.qqbot.test.demo;

import io.github.zimoyin.qqbot.Config;
import io.github.zimoyin.qqbot.LocalLogger;
import io.github.zimoyin.qqbot.bot.Bot;
import io.github.zimoyin.qqbot.bot.message.EmojiType;
import io.github.zimoyin.qqbot.bot.message.MessageChain;
import io.github.zimoyin.qqbot.bot.message.MessageChainBuilder;
import io.github.zimoyin.qqbot.bot.message.type.*;
import io.github.zimoyin.qqbot.event.events.Event;
import io.github.zimoyin.qqbot.event.events.group.GroupEvent;
import io.github.zimoyin.qqbot.event.events.group.operation.OpenGroupBotEvent;
import io.github.zimoyin.qqbot.event.events.message.ChannelMessageEvent;
import io.github.zimoyin.qqbot.event.events.message.MessageEvent;
import io.github.zimoyin.qqbot.event.events.message.PrivateChannelMessageEvent;
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus;
import io.github.zimoyin.qqbot.net.Intents;
import io.github.zimoyin.qqbot.net.Token;
import io.github.zimoyin.qqbot.net.http.TencentOpenApiHttpClient;
import io.github.zimoyin.qqbot.net.http.api.API;
import io.github.zimoyin.qqbot.utils.Async;
import io.vertx.core.Vertx;

import java.io.File;

/**
 * @author : zimo
 * @date : 2024/11/26
 */
public class TMain {
    public static void run(Token token) {
        token.version(1);
        long start = System.currentTimeMillis();
        long start2 = start;
        String url = "http://ts1.cn.mm.bing.net/th/id/R-C.23034dbcaded6ab4169b9514f76f51b5?rik=mSGADwV9o/teUA&riu=http://pic.bizhi360.com/bbpic/40/9640_1.jpg&ehk=RYei4n5qyNCPVysJmE2a3WhxSOXqGQMGJcvWBmFyfdg=&risl=&pid=ImgRaw&r=0";

        LocalLogger logger = new LocalLogger("Main");

        //全局事件监听
        GlobalEventBus.INSTANCE.onEvent(Event.class, true, event -> {
            logger.info("收到事件：" + event.toString());
        });

        System.out.println("Vertx 与组件初始化耗时: " + (System.currentTimeMillis() - start));
        start2 = System.currentTimeMillis();

        TencentOpenApiHttpClient.setSandBox(false);

        Token finalToken = token;
        Bot bot = Bot.createBot(config -> {
            config.setToken(finalToken);
            config.setIntents(Intents.Presets.PRIVATE_GROUP_INTENTS);
        });


        System.out.println("Bot 创建耗时: " + (System.currentTimeMillis() - start));
        System.out.println("Bot 创建耗时: " + (System.currentTimeMillis() - start2));
        start2 = System.currentTimeMillis();

        bot.getConfig().setRetry(99);
//        bot.getContext().set("SESSION_ID", "d5141070-a591-47fa-b334-8ed1eff92ec6");
        API.setDebug(true);
        bot.getContext().set("internal.isAbnormalCardiacArrest", true);
        bot.getContext().set("internal.headerCycle", 5 * 1000);
        bot.getContext().set("PAYLOAD_CMD_HANDLER_DEBUG_LOG", true);
        bot.getContext().set("PAYLOAD_CMD_HANDLER_DEBUG_MATA_DATA_LOG", false);
        bot.getContext().set("PAYLOAD_CMD_HANDLER_DEBUG_HEART_BEAT", false);

        bot.onEvent(OpenGroupBotEvent.class, true, event -> {
            event.reply("你好");
        });

        bot.onEvent(PrivateChannelMessageEvent.class, true, event -> {
            event.reply(ImageMessage.create(new File("D:\\code\\java_kotlin\\ra3_qqbot\\data\\images\\camp_1.png")));
        });


        long finalStart = start2;
        long finalStart1 = start2;
        bot.login().onSuccess(ws -> {
            logger.info("登录成功");
            System.out.println("OK");
            System.out.println("启动耗时: " + (System.currentTimeMillis() - start));
            System.out.println("启动耗时: " + (System.currentTimeMillis() - finalStart));
        }).onFailure(e -> {
            logger.error("登录失败", e);
            bot.close();
            Config.getGLOBAL_VERTX_INSTANCE().close();
            System.out.println("启动耗时: " + (System.currentTimeMillis() - start));
            System.out.println("启动耗时: " + (System.currentTimeMillis() - finalStart1));
        });

    }
}
