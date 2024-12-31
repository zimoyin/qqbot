# QQ_Bot_Framework

<p align="center">
  镇楼图
</p>
<p align="center">
  <img src="logo.png" alt="logo" width="50%" height="50%" />
</p>

----


QQ Bot 是一个使用Kotlin 语言编写的，运行在JVM 平台上的QQ机器人框架。该框架依据于腾讯QQ官方开放API文档进行打造的

QQBot 实现了 WebHook 与 WebSocket 两种方式的连接，并提供了 WebHook 到 WebSocket 的代理服务器功能

> 对于腾讯QQ官方将要在 2024 年年底逐步停用WebSocket 的应对方案，本项目也提供了，WebHook 到 WebSocket 的代理。
>
> 使用 bot.start() 启动 WebSocket，
>
> start 方法参数传入 [WebHookConfig.kt](src/main/kotlin/io/github/zimoyin/qqbot/net/webhook/WebHookConfig.kt) 时，配置 WebHookConfig 中的 websocket 相关配置即可启动 WebSocket
>
> **本项目 release 下的 jar 就是对本功能的调用实现**，第一次启动时将会生成配置文件，第二次会读取配置文件并启动
>
> 默认 websocket 访问路径为 ip:port/websocket
> 其他API访问路径为 ip:port/* 请配置 ws 客户端的 API 访问地址为 https://ip:port
>
> > 启动需要配置SSL
>


* 项目使用 SL4j 2.0.9 注意版本兼容

# 快速搭建(WebHook)
```java
LocalLogger logger = new LocalLogger("Main");
String url = "http://ts1.cn.mm.bing.net/th/id/R-C.23034dbcaded6ab4169b9514f76f51b5?rik=mSGADwV9o/teUA&riu=http://pic.bizhi360.com/bbpic/40/9640_1.jpg&ehk=RYei4n5qyNCPVysJmE2a3WhxSOXqGQMGJcvWBmFyfdg=&risl=&pid=ImgRaw&r=0";

//全局事件监听
GlobalEventBus.INSTANCE.onEvent(Event.class, true, event -> {
    logger.info("收到事件：" + event.toString());
});

TencentOpenApiHttpClient.setSandBox(true);

Bot bot = Bot.createBot(token);
// 监听事件
bot.onEvent(OpenGroupBotEvent.class, true, event -> {
    event.reply("你好");
});

bot.onEvent(MessageEvent.class, true, event -> {
    if (event instanceof ChannelMessageEvent) System.out.println("ChannelMessageEvent 事件");
    else event.reply("你好");
});

bot.onEvent(ChannelMessageEvent.class, true, event -> {
    event.reply(ImageMessage.create(url));
});

WebHookConfig webHookConfig = WebHookConfig
    .builder()
    .sslPath("./127.0.0.1") // SSL 证书保存地址
    .isSSL(true) // 腾讯服务器 WebHook 要求SSL
    .enableWebSocketForwarding(true) // 启用 websocket 转发，客户端访问改 httpserver 服务器的 /websocket 地址即可
    .enableWebSocketForwardingLoginVerify(true)// 启用身份验证，验证客户端身份，验证方式为 webserver 与 websocket 客户端使用同样的机器人的 token 等并使用同样的鉴权方式即可。推荐服务器传入 token 和  clientSecret ，这样客户端两种鉴权方式都支持
    .build();

bot.start(webHookConfig).onSuccess(ws -> {
    logger.info("服务器启动成功,端口: " + ws.getServer().actualPort());
    System.out.println("OK");
}).onFailure(e -> {
    logger.error("登录失败", e);
    bot.close();
    Config.getGLOBAL_VERTX_INSTANCE().close();
});
```

# 快速搭建(WebSocket)
```java
//-----------------------------
//# 快速搭建(WebHook)代码复用    #
//-----------------------------

Bot bot = Bot.createBot(config -> {
    config.setToken(token);
    config.setIntents(Intents.Presets.PRIVATE_GROUP_INTENTS);
});

//-----------------------------
//# 快速搭建(WebHook)代码复用    #
//-----------------------------

// login 参数为 false 时，忽略服务器证书不匹配问题
bot.login().onSuccess(ws -> {
    logger.info("登录成功");
    System.out.println("OK");
}).onFailure(e -> {
logger.error("登录失败", e);
    bot.close();
    Config.getGLOBAL_VERTX_INSTANCE().close();
});
```

# 使用文档

使用前请先去腾讯开发平台申请一个[机器人](https://q.qq.com/#/app/bot)

* [引入依赖](docs%2Fdependent.md)
* [使用文档](docs%2Flogin.md)
* [事件列表](docs%2Fevents.md)
* 注意：所有的异步API(具有Future返回值的API)，必须使用 `onFailure` 方法来处理异常，否则可能会具有一定概率丢失异常信息。如果不使用系统会进行打印部分日志信息。但是不是全部，如果你选择了自己处理那么系统就不会再打印

# 启动转发程序
请从 release 页面下载 Jar 并启动他，之后该程序将会生成配置文件。
配置好后再次启动，他将 WebHook 转为 WebSocket。之后就可以通过原本的 WebSocket Client 程序连接到该服务器


## 体验项目
* 成熟项目体验： 宝可梦
宝可梦文字游戏，内含16个城镇，468个宝可梦，mega形态，20多个神兽。获得8大道馆徽章，解决丰缘危机，登上冠军之路，成为联盟冠军吧
<img src="img.png" alt="描述图片的文字" height="300">
