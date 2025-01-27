## 使用 WebHook 方式登录
登录QQ机器人的方式分为两种 `WebSocket` 和 `WebHook`
* WebSocket: 通过 WebSocket 协议登录，任何一个机器都可以主动连接腾讯服务器进行主动登录（2025初腾讯将不再维护）
* WebHook: 通过 Http 协议被动登录，只有部署在服务器上，并在QQ机器人平台配置，WebHook 的服务器地址才能让腾讯访问 WebHook 服务器，进行登录


### 创建机器人
具有以下重载:
* createBot(token: Token)
* createBot(appid: String, secret: String)
* createBot(appid: String, token: String, secret: String)
```java
// 设置是否启用沙盒环境
TencentOpenApiHttpClient.isSandBox = true
Bot bot = createBot("appid", "token", "secret");
```

### 登录机器人
登录机器人使用 `start` 方法，因为 WebHook 就是在本地启动一个服务器，所以 `start` 方法就是启动服务器，然后等待腾讯服务器访问
```kotlin
bot.start();
```
该 start 具有一个参数 WebHookConfig，通过配置 WebHookConfig 来定义服务器行为

### 创建 WebHookConfig
```java
WebHookConfig webHookConfig = WebHookConfig
    .builder()
    .sslPath("./127.0.0.1")
    .isSSL(true)
    .enableWebSocketForwarding(true)
    .enableWebSocketForwardingLoginVerify(true)
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
> 各种配置解释
1. sslPath : 配置 SSL 证书所在的文件夹路径，如果不使用 SSL 请将 isSSL 设置为 false
2. isSSL : 是否使用 SSL，如果启用了SSL，则需要配置 sslPath
3. port: 监听的端口，默认为 443, 如果是 0 则随机分配一个端口
4. password: 密码，如果启用了SSL，并且证书需要密码的话则需要配置 password
5. host: 监听的域名，默认为 0.0.0.0
6. options： 是 HttpServerOptions 如果需要自定义一些 HttpServer 的配置，则可以使用,但是注意使用后，isSSL 等 ssl 配置将不会生效了，需要自行在 options 配置
7. enableWebSocketForwarding: 是否启用 WebSocket 转发，默认为 true
8. webSocketPath： WebSocket 转发路径，默认为 /websocket
9. enableWebSocketForwardingLoginVerify： 是否启用 WebSocket 转发登录验证，默认为 true。服务器需要验证客户端的 token 等信息
10. enableWebSocketForwardingIntentsVerify ： 是否启用 WebSocket 转发 Intents 验证，默认为 false。服务器需要验证客户端的 Intents 等信息

### 什么是 WebHook 转发 (enableWebSocketForwarding)
enableWebSocketForwarding 开启后，将会在服务器启动后，在 `webSocketPath` 路径启动一个 WebSocket 处理程序。
该程序的作用是将 WebHook 接收到的数据转发给客户端，客户端可以监听到这些数据，并进行处理。
该配置的初衷是，服务器代码打包发布过于麻烦，如果服务器作为一个转发，将数据转发到本地则可以进行本地代码调试

> 如何让客户端连接到转发服务器，即 开启了 enableWebSocketForwarding 的WebHook服务器
```java
// 配置服务器地址
String host = "zimoyin.xyz:8080";
TencentOpenApiHttpClient.setHost(host);
// 配置使用自定义服务器地址(设置 Host 后默认为 true)
TencentOpenApiHttpClient.setUseCustomHost(true);
// 配置使用默认 websocket 处理路径。如果不配置则是访问 /websocket
//    TencentOpenApiHttpClient.webSocketForwardingAddress = "wss://"+host+"/ws"

// 创建机器人
// bot.context.put("client_use_ssl",false) //设置客户端不使用 SSL
TencentOpenApiHttpClient.isSandBox = true
Bot bot = Bot.INSTANCE.createBot(config -> {
    // 创建机器人时，需要设置需要监听的事件。 以下使用 Intents 权限配置来称呼他
    config.setIntents(Intents.Presets.PRIVATE_INTENTS);
    config.setToken(appid, token, appsecret); // token 与 appsecret 允许各选其一
});

bot.login(); // 如果无法连接服务器，可能是证书错误, 需要使用 bot.login(false) 忽略 服务器证书与服务器IP不匹配问题
```


