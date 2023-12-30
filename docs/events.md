### 主要事件类型如下
其余事件类型请自行查询源码
* [Event.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2FEvent.kt) 所有事件的基础类
* 信息事件 [MessageEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Fmessage%2FMessageEvent.kt)
  * AT 事件 [AtMessageEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Fmessage%2Fat%2FAtMessageEvent.kt)
    * 频道中@机器人事件 (公域机器人只能通过 @ 来监听内容) [ChannelAtMessageEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Fmessage%2Fat%2FChannelAtMessageEvent.kt)
    * 群中@机器人事件 (机器人只能通过 @ 来监听内容)  [GroupAtMessageEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Fmessage%2Fat%2FGroupAtMessageEvent.kt)
  * 私信 [PrivateMessageEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Fmessage%2Fdirect%2FPrivateMessageEvent.kt)
    * 频道私信 [ChannelPrivateMessageEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Fmessage%2Fdirect%2FChannelPrivateMessageEvent.kt)
    * 朋友聊天事件 [UserPrivateMessageEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Fmessage%2Fdirect%2FUserPrivateMessageEvent.kt)
  * 频道信息 [ChannelMessageEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Fmessage%2FChannelMessageEvent.kt)
    * 频道信息事件 (私域机器人能监听改频道信息，不需要 @) [PrivateChannelMessageEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Fmessage%2FPrivateChannelMessageEvent.kt)
* 上下线事件
  * 机器人第一次发起网络请求，服务器并成功的进行了回应 [BotHelloEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Fplatform%2Fbot%2FBotHelloEvent.kt)
  * 登录 [BotOnlineEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Fplatform%2Fbot%2FBotOnlineEvent.kt)
  * 机器人需要重连的通知事件 [BotReconnectNotificationEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Fplatform%2Fbot%2FBotReconnectNotificationEvent.kt)
  * 重连 [BotReconnectNotificationEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Fplatform%2Fbot%2FBotReconnectNotificationEvent.kt)
  * 下线 [BotOfflineEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Fplatform%2Fbot%2FBotOfflineEvent.kt)
* 信息撤回 [MessageRevokeEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Frevoke%2FMessageRevokeEvent.kt)
  * 频道私信撤回 [ChannelPrivateMessageRevokeEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Frevoke%2FChannelPrivateMessageRevokeEvent.kt)
  * 频道信息撤回 [ChannelMessageRevokeEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Frevoke%2FChannelMessageRevokeEvent.kt)
  * 犹豫没有其他聊天场景下的撤回信息的的原文暂时没有其他聊天场景的撤回信息的类型
* 信息机器人发送监听与修改
  * 频道信息发射前 [ChannelMessageSendPreEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Fplatform%2FChannelMessageSendPreEvent.kt)
  * 频道信息发射  [ChannelMessageSendEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Fplatform%2FChannelMessageSendEvent.kt)
  * 信息发射被拦截 [MessageSendInterceptEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Fplatform%2FMessageSendInterceptEvent.kt)
  * 信息发射前 [MessageSendPreEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Fplatform%2FMessageSendPreEvent.kt)
  * 信息发射 [MessageSendEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Fplatform%2FMessageSendEvent.kt)
  * 请通过以下方法添加信息的拦截器，否则你无法修改即将要发送的信息
```kotlin
MessageSendPreEvent.interceptor {
    return@interceptor it.apply {
        intercept = false
        messageChain = messageChain
    }
}
```
* 频道信息表情粘贴事件 [MessagePasteEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Fpaste%2FMessagePasteEvent.kt)
  * 粘贴表情 [MessageAddPasteEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Fpaste%2FMessageAddPasteEvent.kt)
  * 删除表情 [MessageDeletePasteEvent.kt](src%2Fmain%2Fkotlin%2Fgithub%2Fzimoyin%2Fevent%2Fevents%2Fpaste%2FMessageDeletePasteEvent.kt)
