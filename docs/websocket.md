## 使用 WebSocket 方式登录
登录QQ机器人的方式分为两种 `WebSocket` 和 `WebHook`
* WebSocket: 通过 WebSocket 协议登录，任何一个机器都可以主动连接腾讯服务器进行主动登录（2025初腾讯将不再维护）
* WebHook: 通过 Http 协议被动登录，只有部署在服务器上，并在QQ机器人平台配置，WebHook 的服务器地址才能让腾讯访问 WebHook 服务器，进行登录

### 创建机器人
> Java：创建方式(众多重载方法的一种)
```java
// 设置是否启用沙盒环境
TencentOpenApiHttpClient.isSandBox = true
// 创建机器人有很多的重载,这里使用比较推荐的
Bot bot = Bot.INSTANCE.createBot(config -> {
    // 创建机器人时，需要设置需要监听的事件。 以下使用 Intents 权限配置来称呼他
    config.setIntents(Intents.Presets.PRIVATE_INTENTS);
    config.setToken(appid, token, appsecret); // token 与 appsecret 允许各选其一
});
// 设置重试次数，如果设置的小于 -1 则无限次重试
BotConfig config = bot.getConfig();
config.setReconnect(true); // 断线是否重连
config.setRetry(99);
```
> Kotlin: 创建方式(众多重载方法的一种)
```kotlin
// 设置是否启用沙盒环境
TencentOpenApiHttpClient.isSandBox = true
Bot.createBot{
    it.setToken(token)
    it.setIntents(Intents.Presets.PRIVATE_GROUP_INTENTS)
}
```

### 登录机器人
```java
bot.login();
// bot.login(false) //忽略服务器错误的证书
```

### Intents
Intents 是一个权限配置，用于控制机器人的权限，框架提供了 Intents 和 Intents.Presets
* Intents.Presets 是框架预先配置好的，可以直接使用
* Intents 是一个枚举，可以自己组合使用

1. 使用 Intents.Presets
直接通过 Intents.Presets. 即可访问

2. 使用 Intents
```java
int intent = Intents.create(Intents.GROUP_INTENTS, Intents.OPEN_FORUMS_EVENT,...)
```

3. kotlin 相加
```kotlin
val intent = Intents.GROUP_INTENTS + Intents.GUILD_MESSAGES + Intents.DIRECT_MESSAGE
```

4. 拓展 Intents.Presets.PRIVATE_GROUP_INTENTS
```java
Intents.create(Intents.Presets.PRIVATE_GROUP_INTENTS.code,Intents.START.code)
```
