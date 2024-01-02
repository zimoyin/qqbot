### 登录
登录方式很多种，以下为通用的登录方式。
> 其余方式见 [Bot](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fbot%2FBot.kt) 提供的各种 createBot 方法的重载

Kotlin:
```kotlin
Bot.INSTANCE.createBot() {
    it.setIntents(github.zimoyin.net.Intents.Presets.PRIVATE_INTENTS) //设置权限
    it.setToken(appid, token, appsecret) // token 与 appsecret 允许各选其一
}.login()
```
Java:
```java
Bot bot = Bot.INSTANCE.createBot(config -> {
    config.setIntents(Intents.Presets.PRIVATE_INTENTS);
    config.setToken(appid, token, appsecret); // token 与 appsecret 允许各选其一
});
bot.login();
```
### 选择鉴权方式
鉴权是通过 [Token.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fnet%2FToken.kt) 进行的，他会根据选择鉴权版本信息(token 与 appsecret 允许各选其一)来进行 Token 获取与更新

### 事件监听
事件监听方式分为全局Bot全局监听与Bot全局监听
#### 全局监听
注意对全局事件进行监听只能监听来自于同一个 Vertx 的事件，如果想要跨 Vertx 监听需要自行组件Vert 集群（请自行查阅资料）
```kotlin
//通过全局事件总线来对全局事件进行监听
GlobalEventBus.onEvent<Event> {
    println("全局事件监听: " + it.metadataType)
}
//通过Bot使用的事件总线来对全局事件进行监听
//... 省略获取 bot
bot.onEvent<MessageEvent> {
    //尝试发送信息
    it.reply(it.messageChain)
}
```
```java
GlobalEventBus.INSTANCE.onEvent(Event.class, event -> {
    System.out.println(event.getMetadataType());
});
//省略 bot 方式
```
#### Bot监听
Kotlin:
```kotlin
//监听该BOT的全局事件
GlobalEventBus.onBotEvent<Event>(token.appID) {
    println("BOT全局事件监听: " + it.metadataType)
}
GlobalEventBus.onBotEvent<Event>(bot) {
  println("BOT全局事件监听: " + it.metadataType)
}
//... 省略获取 bot
bot.onVertxEvent<Event> {  } //监听该Bot的Vertx事件
bot.onEvent<Event> {  }
```
Java:
```java
GlobalEventBus.INSTANCE.onBotEvent(bot,Event.class, event -> {
  System.out.println(event.getMetadataType());
});
GlobalEventBus.INSTANCE.onBotEvent(token.appID,Event.class, event -> {
  System.out.println(event.getMetadataType());
});
//省略其他
```
### 关于框架中的 HTTP API 返回的不是值而是一个 Future
为了系统的性能所有操作都是异步的，如果你需要阻塞等待结果你可以使用 `await()` 方法或者 `awaitToCompleteExceptionally()` 方法。前者是阻塞协程，后者阻塞线程。

对于 java 来说没有任何等待函数，或许以后 awaitToCompleteExceptionally 会在工具类中出现方便 java 使用。
或者使用以下的调用链
```kotlin
future.toCompletionStage().toCompletableFuture().get() //阻塞线程并获取值

```

### 关于自定义事件
[前往文档](CustomEvent.md)

### 关于如何主动的复用 Session 进行重连服务器
每次链接服务器后，服务器都会下发 Session 方便机器人重连。该Session通常会存在一天作业，超过就会失效
以下是复用Session的示例
```kotlin
bot.context["SESSION_ID"] = "SessionID"
bot.login()
```

### bot 上下文
对于Session的复用是通过对bot 上下文进行设置后进行的，那么上面是bot上下文？
Bot上下文是Bot的全局变量每个Bot都有自己的上下文，你可以在任何地方使用，他可以存储信息等，方便你跨事件数据传输或者报错。
Bot提供了 set/get 方法来报错每一个信息。
Bot 还提供的`设置栈`方法，提供该方法，你可以查找到是哪个位置设置的该上下文.

[源码](..%2Fsrc%2Fmain%2Fkotlin%2Fcom%2Fgithub%2Fzimoyin%2Fqqbot%2Fbot%2FBotContent.kt)
