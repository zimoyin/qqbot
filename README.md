# QQ_Bot_Framework

QQ Bot 是一个使用Kotlin 语言编写的，运行在JVM 平台上的官方适配QQ机器人框架。该框架依据于腾讯QQ官方开发API文档进行打造的
QQBot 以及实现了 WebHook 与 WebSocket 两种方式的连接

> 对于腾讯QQ官方将要在 2024 年年底逐步停用WebSocket 的应对方案，本项目也提供了，WebHook 到 WebSocket 的代理。
> 使用 bot.start() 启动 WebSocket 时，传入 [WebHookConfig.kt](src/main/kotlin/io/github/zimoyin/qqbot/net/webhook/WebHookConfig.kt) 时，配置 WebHookConfig 启动WebSocket 即可
> 本项目 release 下的jar 就是对本功能的调用实现，第一次启动时将会生成配置文件，第二次会读取配置文件并启动
> > 启动需要配置SSL
>


* 项目使用 SL4j 2.0.9 注意版本兼容

# 快速搭建
```java

```

# 使用文档

使用前请先去腾讯开发平台申请一个[机器人](https://q.qq.com/#/app/bot)

* 注意：所有的异步API(具有Future返回值的API)，必须使用 `onFailure` 方法来处理异常，否则可能会具有一定概率丢失异常信息。如果不使用系统会进行打印部分日志信息。但是不是全部，如果你选择了自己处理那么系统就不会再打印
* [引入依赖](docs%2Fdependent.md)
* [使用文档](docs%2Flogin.md)
* [事件列表](docs%2Fevents.md)

# 启动转发程序
请从 release 页面下载 Jar 并启动他，之后该程序将会生成配置文件。
配置好后再次启动，他将 WebHook 转为 WebSocket。之后就可以通过原本的 WebSocket Client 程序连接到该服务器


## 体验项目
* 成熟项目体验： 宝可梦
宝可梦文字游戏，内含16个城镇，468个宝可梦，mega形态，20多个神兽。获得8大道馆徽章，解决丰缘危机，登上冠军之路，成为联盟冠军吧
![img.png](img.png)
