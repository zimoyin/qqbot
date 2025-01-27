### 1. 登录

## 使用 WebSocket 方式登录

登录QQ机器人的方式分为两种 `WebSocket` 和 `WebHook`

* WebSocket: 通过 WebSocket 协议登录，任何一个机器都可以主动连接腾讯服务器进行主动登录（2025初腾讯将不再维护）
* WebHook: 通过 Http 协议被动登录，只有部署在服务器上，并在QQ机器人平台配置，WebHook 的服务器地址才能让腾讯访问 WebHook
  服务器，进行登录

1. [参阅 WebSocket 登录方式](websocket.md)
2. [参阅 WebHook   登录方式](webhook.md)

#### 1.1 退出登录

1. 退出登录
   * 通过 `login()` 获取到的 `WebSocketClient` 实例，调用 `close()` 方法关闭连接
   * 通过 `start()` 获取到的 实例，调用 `close()` 方法关闭连接
2. 退出程序
   如果没有自己创建 Vertx 的话，使用 `GLOBAL_VERTX_INSTANCE.close()` 即可关闭

### 2. 选择鉴权方式

鉴权是通过 [Token.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fnet%2FToken.kt) 进行的，他会根据选择鉴权版本信息(token 与
appsecret 允许各选其一)来进行 Token 获取与更新

### 3. 事件监听

事件监听方式分为全局Bot全局监听与Bot全局监听
注意：对于群里只有文本类型的信息才能触发事件
注意：事件所在协程为，Vertx 的事件循环线程池协程调度器，所以请不要在事件中进行阻塞操作，否则可能会导致Vertx
阻塞。如果需要执行阻塞代码或者高CPU代码，请切换到其他协程或者使用 Vertx 的工作线程。

#### 3.1 全局监听

注意对全局事件进行监听只能监听来自于同一个 Vertx 的事件，如果想要跨 Vertx 监听需要自行组件Vert 集群（请自行查阅资料）
Kotlin:

```kotlin
//通过全局事件总线来对全局事件进行监听
GlobalEventBus.onEvent<Event> {
    println("全局事件监听: " + it.metadataType)
}
// 简便方式使用工作线程
GlobalEventBus.onEvent<Event>(true) {
    println("全局事件监听: " + it.metadataType)
}
//通过Bot使用的事件总线来对全局事件进行监听
//... 省略获取 bot
bot.onEvent<MessageEvent> {
    //尝试发送信息
    it.reply(it.messageChain)
}
```

Java:

```java
GlobalEventBus.INSTANCE.onEvent(Event .class, event ->{
    System.out.

println(event.getMetadataType());
    });
//省略 bot 方式
```

#### 3.2 Bot监听

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
bot.onVertxEvent<Event> { } //监听创建该Bot 的 Vertx 中所有的事件
bot.onEvent<Event> { } // 监听该Bot的事件
```

Java:

```java
GlobalEventBus.INSTANCE.onBotEvent(bot, Event .class, event ->{
    System.out.println("BOT全局事件监听"+event.getMetadataType());
});
GlobalEventBus.INSTANCE.onBotEvent(token.appID, Event .class, event ->{
    System.out.println("BOT全局事件监听"+event.getMetadataType());
});
// 机器人事件监听，只监听和该机器人有关的事件
bot.onEvent(Event .class, event ->{
    System.out.println("收到机器人事件");
});
//省略其他
```

### 4. 关于框架中的 HTTP API 返回的不是值而是一个 Future

为了系统的性能所有操作都是异步的，如果你需要阻塞等待结果你可以使用 `await()` 方法或者 `awaitToCompleteExceptionally()`
方法。前者是阻塞协程，后者阻塞线程。

对于 java 来说没有任何等待函数，或许以后 awaitToCompleteExceptionally 会在工具类中出现方便 java 使用。
或者使用以下的调用链

```kotlin
future.toCompletionStage().toCompletableFuture().get() //阻塞线程并获取值

```

### 5. 关于自定义事件

[前往文档](CustomEvent.md)

### 关于如何主动的复用 Session 进行重连服务器

每次链接服务器后，服务器都会下发 Session 方便机器人重连。该Session通常会存在一天作业，超过就会失效
以下是复用Session的示例

```kotlin
bot.context["SESSION_ID"] = "SessionID"
bot.login()
```

### 6. bot 上下文

对于Session的复用是通过对bot 上下文进行设置后进行的，那么什么是bot上下文？
Bot上下文是Bot的全局变量每个Bot都有自己的上下文，你可以在任何地方使用，他可以存储信息等，方便你跨事件数据传输或者报错。
Bot提供了 set/get 方法来报错每一个信息。
Bot 还提供的`设置栈`方法，提供该方法，你可以查找到是哪个位置设置的该上下文.

```kotlin
// 通过该方法可以查看设置记录，如最后一次记录的堆栈位置
context.getRecord("key")
```

框架提供的可修改默认上下文参数

```java
BotConfig config = bot.getConfig();
config.setRetry(99);
bot.getContext().set("SESSION_ID","60a176e1-2790-4bf0-85cd-c123763981ea"); // 设置Session ID 用于复用已经存在的会话。注意：适用于沙盒环境，正式环境请谨慎使用
bot.getContext().set("SESSION_ID_Failure_Reconnection",true); // 会话ID失效则废弃 Session id 重新连接服务器
bot.getContext().et("gatewayURL","wss://sandbox.api.sgroup.qq.com/websocket/"); // 硬编码设置wss接入点同时shards设置为1.不推荐使用(// config.shards = 1)
bot.getContext().set("PAYLOAD_CMD_HANDLER_DEBUG_LOG",true);    // 命令处理器日志
bot.getContext().set("PAYLOAD_CMD_HANDLER_DEBUG_MATA_DATA_LOG",false); // 命令元数据日志
bot.getContext().set("PAYLOAD_CMD_HANDLER_DEBUG_HEART_BEAT",false);// 心跳日志,不能单独开启应该与上面两个其中一个一并开启
bot.getContext().set("internal.isAbnormalCardiacArrest",true); //如果心跳在 心跳周期 + 30s 内没有发送出去就抛出异常
bot.etContext().set("internal.headerCycle",5*1000); // 修改心跳周期
bot.getContext().set("client_use_ssl",false); // WebSocket 禁用 ssl
bot.getContext().set("newconnecting",true); // 断线是否重连


// 只读上下文
bot.context["shards"] // 分片
bot.context["internal.throwable"]
bot.context["internal.promise"] // 登录 的promise
bot.context["ws"] // Bot 的 WebSocket 实例
bot.context["internal.handler"] // PayloadCmdHandler
// ....
```

[源码](..%2Fsrc%2Fmain%2Fkotlin%2Fcom%2Fgithub%2Fzimoyin%2Fqqbot%2Fbot%2FBotContent.kt)

### 7. 信息类型

已经支持的信息类型:
信息类型列表: https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/type/text.html

### 8. 建议屏蔽的日志

```java
io.vertx.core.logging.LoggerFactory
io.netty
```

### 3. [示例文档](%E7%A4%BA%E4%BE%8B.md)

### 4. [HttpClientAPI.md](HttpClientAPI.md)
