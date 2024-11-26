package io.github.zimoyin.qqbot.test.demo;

import com.github.zimoyin.qqbot.Config;
import com.github.zimoyin.qqbot.bot.Bot;
import com.github.zimoyin.qqbot.bot.message.MessageChain;
import com.github.zimoyin.qqbot.bot.message.MessageChainBuilder;
import com.github.zimoyin.qqbot.bot.message.type.ImageMessage;
import com.github.zimoyin.qqbot.event.events.Event;
import com.github.zimoyin.qqbot.event.events.message.MessageEvent;
import com.github.zimoyin.qqbot.event.supporter.GlobalEventBus;
import com.github.zimoyin.qqbot.net.Intents;
import com.github.zimoyin.qqbot.net.Token;
import com.github.zimoyin.qqbot.net.bean.message.MessageMarkdown;
import com.github.zimoyin.qqbot.net.http.api.TencentOpenApiHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : zimo
 * @date : 2024/11/26
 */
public class TMain {
    public static void run(Token token) {
        String url = "http://ts1.cn.mm.bing.net/th/id/R-C.23034dbcaded6ab4169b9514f76f51b5?rik=mSGADwV9o/teUA&riu=http://pic.bizhi360.com/bbpic/40/9640_1.jpg&ehk=RYei4n5qyNCPVysJmE2a3WhxSOXqGQMGJcvWBmFyfdg=&risl=&pid=ImgRaw&r=0";

        Logger logger = LoggerFactory.getLogger("Main");
        //全局事件监听
        GlobalEventBus.INSTANCE.onEvent(Event.class,true, event -> {
            logger.info("收到事件：{}", event);
        });

        TencentOpenApiHttpClient.setSandBox(false);

        Bot bot = Bot.createBot(config->{
            config.setToken(token);
            config.setIntents(Intents.Presets.PRIVATE_GROUP_INTENTS);
        });

        bot.getConfig().setRetry(99);
//        bot.getContext().set("SESSION_ID", "d5141070-a591-47fa-b334-8ed1eff92ec6");
        bot.getContext().set("PAYLOAD_CMD_HANDLER_DEBUG_LOG", true);
        bot.getContext().set("PAYLOAD_CMD_HANDLER_DEBUG_MATA_DATA_LOG", false);
        bot.getContext().set("PAYLOAD_CMD_HANDLER_DEBUG_HEART_BEAT", false);

        bot.onEvent(MessageEvent.class, event ->{
//            MessageChain chain = new MessageChainBuilder().append(ImageMessage.create(url)).append("你好").build();
//            event.quote(chain).onFailure(e->{
//                logger.error("发送失败",e);
//            });

            MessageChain messageChain = MessageMarkdown.create("102077167_1706091638")
                .appendParam("date", "123")
                .appendParam("rw", event.getMessageChain().content())
                .build()
                .toMessageChain();

            System.out.println(event.getMessageChain().content());
            event.reply(messageChain).onFailure(e -> {
                logger.error("发送失败", e);
            }).onSuccess(r -> {
                logger.info("发送成功: "+r);
            });
        });


        bot.login().onSuccess(ws->{
            logger.info("登录成功");
            System.out.println("OK");
        }).onFailure(e->{
            logger.error("登录失败",e);
            bot.close();
            Config.getGLOBAL_VERTX_INSTANCE().close();
        });
    }
}