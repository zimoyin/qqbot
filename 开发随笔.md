
ws:  vertx
http:  vertx+jdk
json: vertx
event bus:  vertx


三种鉴权方式，这里选择两种
1. https://bot.q.qq.com/wiki/develop/api-v2/dev-prepare/interface-framework/api-use.html#%E9%89%B4%E6%9D%83%E6%96%B9%E5%BC%8F
2. https://bot.q.qq.com/wiki/develop/api/#%E7%A5%A8%E6%8D%AE 第一个


## 机器人服务点切片
1. 机器人切片是指将一个机器人分成多个子机器人，每个子机器人负责处理一部分频道或者群消息。
2. 通过 Bot 的 Config 的 Shares 来设置切片。该切片形式不需要 Vertx 集群，同时也是推荐的集群方式
## 机器人事件处理点切片
1. 机器人事件处理点切片是指将一个机器人事件处理点分成多个子客户端，每个子客户端负责处理一部分类型的事件。
2. 机器人事件处理点切片需要 Vertx 集群，
3. 通过全局事件总线即可监听，推荐每个机器监听不同事件类型
4. 注意：你需要自行安装 vertx 的集群管理器，并配置集群
5. 注意：使用集群的时候你无法在 Event 中获取到 Bot 实例，但是你可以获取到 Bot 信息
6. 如果在集群里广播全局的自定义事件请使用 EventBus 全局事件总线

## 关闭系统
GLOBAL_VERTX_INSTANCE.close()

## 监听事件
注意：事件注册要在登录之前，否则会遗失部分事件
1. 通过 EventBus 全局事件总线。该事件总线是默认的，如果机器人没有使用其他Vertx 都会通过该事件总线分发事件
2. 通过 Bot 的 config 的事件总线，如果你使用了自己的Vertx 创建的机器人那么请使用他，否则无法监听到事件


任务列表: 
1. 单聊/群聊信息发送
2. API 张贴表情 https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/trans/emoji.html#%E6%9C%BA%E5%99%A8%E4%BA%BA%E5%8F%91%E8%A1%A8%E8%A1%A8%E6%83%85%E8%A1%A8%E6%80%81
3. API 张贴表情 https://bot.q.qq.com/wiki/develop/api/openapi/reaction/put_message_reaction.html
4. 待实现事件: 音频事件 https://bot.q.qq.com/wiki/develop/api/gateway/audio.html#audio-start
5. 待实现事件: 开放论坛事件 https://bot.q.qq.com/wiki/develop/api/gateway/open_forum.html#oepn-forum-event-intents-open-forum-event
6. 待实现事件: 开放论坛事件 https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/open_forum.html
7. 日志系统，通过事件监听来构建日志
8. BUG 无法在API 客户端的回调中抛出一个具有堆栈跟踪的异常。所有的异步API都会有执行结果回调

## Logger 
事件 log 构造
1. 事件的构造 [事件ID:机器人appID]
2. ws的构造 [追踪ID]
3. PayloadCmdHandler的构造 [ws追踪ID]

## 广播自定义事件
1. 继承事件或者继承 Event
2. 在 EventMapping 注册事件，通过 EventMapping.add 方法注册，允许被重复注册
3. 使用 EventBus 事件总线广播事件。可以使用 bot 的EventBus 或者全局的 EventBus。或者 直接使用 bot 的onEvent
4. 为了事件能在网络事件总线上传播请继承 Serializable 方便序列化


所有的异步都可以通过 .await() 或者 .awaitToCompleteExceptionally() 函数获取结果，一个是阻塞协程，一个是阻塞线程