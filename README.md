# QQ_Bot_Framework

依据于腾讯QQ官方开发API文档打造的，位于JVM平台的QQ Bot 开发框架。适用于 Kotlin/Java 开发
如果您喜欢本项目请点击一下 star 支持一下！

# 使用文档

使用前请先去腾讯开发平台申请一个[机器人](https://q.qq.com/#/app/bot)

* 注意：所有的异步API(具有Future返回值的API)，必须使用 `onFailure` 方法来处理异常，否则可能会具有一定概率丢失异常信息。如果不使用系统会进行打印部分日志信息。但是不是全部，如果你选择了自己处理那么系统就不会再打印
* [引入依赖](docs%2Fdependent.md)
* [使用文档](docs%2Flogin.md)
* [事件列表](docs%2Fevents.md)
