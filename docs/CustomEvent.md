# 自定义事件
对于机器人的事件分为两种一种是服务器下发的事件，一种是自定义事件，他们区别如下
* 服务器事件： 通过向EventMapping 注册事件后，当服务器下发符合该事件的消息时，会触发该事件，
* 自定义事件： 通过向EventMapping 注册事件后，只有手动发布该事件，才会触发该事件。

> **注意**: 所有的事件下的任何字段都必须要实现 `Serializable` 接口

## 1. 自定义事件
自定义事件需要通过EventMapping 注册事件，然后通过发布事件的方式触发事件。
### 2.1 创建事件
你需要继承 `CustomEvent` 类，并实现以下字段
* metadata: 事件的原始信息，通常为服务器事件的原始JSON。如果是非服务器事件，则为空字符串获取该事件的名称
* metadataType: 事件类型，由用户自己指定。注意事件类型不能于服务器事件类型相一致，否则该事件为服务器事件
* botInfo: 触发该事件的机器人信息
```kotlin
class TestEvent : CustomEvent() {
  //metadata 使用默认信息 当前类名
  //metadataType 使用默认信息 当前类名
  //botInfo 使用默认信息，使用空的bot 信息，如非必要，请不要使用空的bot信息
  override val botInfo: BotInfo
    get() = BotInfo.emptyBotInfo()
}
```

## 2. 服务器事件
目前基本所有的服务器事件已经被映射到EventMapping中，不需要额外的事件了
于上面自定义事件的创建类似，你需要实现 `Event` 接口,并复写上面全部的方法。并使用以下的注解进行注释
> @EventAnnotation.EventMetaType("Not_MetaType_CustomEvents") //事件类型
> @EventAnnotation.EventHandler(NoneEventHandler::class, false) //事件处理器，并指定禁止忽略处理器实现

* 处理器
他是用于创建该事件的工厂，用于创建该事件的实例，并将该实例传递给事件处理器
你需要继承 [AbsEventHandler.kt](..%2Fsrc%2Fmain%2Fkotlin%2Fcom%2Fgithub%2Fzimoyin%2Fqqbot%2Fevent%2Fsupporter%2FAbsEventHandler.kt) 类，并实现方法并返回该事件的实例对象
**注意:** 如果该事件是一个接口则你必须要由一个实现类实现该接口，并返回该实现类的实例对象


## 3. 广播事件
事件广播由两种方式进行，一种通过全局的事件总线进行，一种是通过Bot所在的事件总线进行

1. 全局事件总线
```kotlin
// 监听事件
GlobalEventBus.onEvent<TestEvent> {
  println(it)
}
//广播事件
GlobalEventBus.broadcastAuto(TestEvent())
```

2. Bot所在的事件总线

```kotlin
val botEventBus = bot.config.botEventBus
// 监听事件
botEventBus.onEvent<TestEvent> {
  println(it)
}
//广播事件
botEventBus.broadcastAuto(TestEvent())
```
