package io.github.zimoyin.qqbot.bot


import io.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.Contact
import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.event.supporter.BotEventBus
import io.github.zimoyin.qqbot.event.supporter.EventMapping
import io.github.zimoyin.qqbot.exception.EventBusException
import io.github.zimoyin.qqbot.net.Intents
import io.github.zimoyin.qqbot.net.Token
import io.github.zimoyin.qqbot.net.bean.GuildBean
import io.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.github.zimoyin.qqbot.net.http.api.channel.getGuildInfos
import io.github.zimoyin.qqbot.net.http.api.channel.getGuilds
import io.github.zimoyin.qqbot.net.webhook.WebHookConfig
import io.github.zimoyin.qqbot.net.webhook.WebHookHttpServer
import io.github.zimoyin.qqbot.utils.vertx
import io.github.zimoyin.qqbot.utils.vertxWorker
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.Message
import io.vertx.core.eventbus.MessageConsumer
import io.vertx.core.http.WebSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable
import java.util.function.Consumer


/**
 *
 * @author : zimo
 * @date : 2023/12/06/23:51
 * BOT 两种主要创建方式
 * Bot.INSTANCE.createBot(appid, token, <appSecret>, intents)
 *
 * BotConfigBuilder().setToken(appid, token, <appSecret>).setIntents(intents).createBot()
 */
interface Bot : Serializable, Contact {
    companion object INSTANCE {
        private val map = HashMap<String, Bot>()

        /**
         * 创建BOT
         * @param token Token
         * @param intents 订阅事件
         */
        @Deprecated("The official has abandoned the WebSocket method")
        @JvmStatic
        fun createBot(token: Token, intents: Int): Bot {
            val config = BotConfigBuilder().setToken(token).setIntents(intents).build()
            val botImp = BotImp(config.token, config = config)
            map[config.token.appID] = botImp
            return botImp
        }

        /**
         * 创建BOT
         * @param token Token
         * @param intents 订阅事件
         */
        @Deprecated("The official has abandoned the WebSocket method")
        @JvmStatic
        fun createBot(token: Token, intents: Intents.Presets): Bot {
            val config = BotConfigBuilder().setToken(token).setIntents(intents).build()
            val botImp = BotImp(config.token, config = config)
            map[config.token.appID] = botImp
            return botImp
        }

        @JvmStatic
        fun createBot(token: Token): Bot {
            val config = BotConfigBuilder().setToken(token).build()
            val botImp = BotImp(config.token, config = config)
            map[config.token.appID] = botImp
            return botImp
        }

        @JvmStatic
        fun createBot(appid: String, secret: String): Bot {
            val config = BotConfigBuilder().setToken(Token.createByAppSecret(appid, secret)).build()
            val botImp = BotImp(config.token, config = config)
            map[config.token.appID] = botImp
            return botImp
        }

        @JvmStatic
        fun createBot(appid: String, token: String, secret: String): Bot {
            val config = BotConfigBuilder().setToken(Token.create(appid, token, secret)).build()
            val botImp = BotImp(config.token, config = config)
            map[config.token.appID] = botImp
            return botImp
        }

        @Deprecated("The official has abandoned the WebSocket method")
        @JvmName("kotlinCreateBot")
        fun createBot(token: Token, callback: BotConfigBuilder.() -> Unit): Bot {
            val config = BotConfigBuilder().setToken(token).apply { callback() }.build()
            val botImp = BotImp(config.token, config = config)
            map[config.token.appID] = botImp
            return botImp
        }

        @Deprecated("The official has abandoned the WebSocket method")
        @JvmStatic
        fun createBot(callback: Consumer<BotConfigBuilder>): Bot {
            val config = BotConfigBuilder().apply { callback.accept(this) }.build()
            val botImp = BotImp(config.token, config = config)
            map[config.token.appID] = botImp
            return botImp
        }

        @Deprecated("The official has abandoned the WebSocket method")
        @JvmStatic
        fun createBot(configBuilder: BotConfigBuilder): Bot {
            val config = configBuilder.build()
            val botImp = BotImp(config.token, config = config)
            map[config.token.appID] = botImp
            return botImp
        }

        /**
         * 根据 appid 获取机器人
         * 为说明使用 appid 作为 key,而不是 qq 号 作为 key，因为对于开发者来说 appid 用处bi qqID 多，并且 appid 也是唯一的
         */
        @JvmStatic
        operator fun get(appid: String): Bot? {
            return map[appid]
        }

        @JvmStatic
        fun getBot(appid: String): Bot {
            return map[appid] ?: throw NullPointerException("Bot is null.")
        }

        @JvmStatic
        fun getBots(): List<Bot> {
            return map.map { it.value }.toList()
        }
    }


    /**
     * 机器人上下文
     */
    val context: BotContent

    /**
     * 机器人配置项
     */
    val config: BotConfig

    /**
     * 登录
     * 警告： 如果使用 await 后会严重阻塞协程
     * @param isVerifyHost 是否验证主机,如果 SSL 证书无法解析的话，请设置为 false
     */
    fun login(isVerifyHost: Boolean): Future<WebSocket>
    fun login(): Future<WebSocket>

    /**
     * 启动机器人 webhook 服务器
     * 警告： 如果使用 await 后会严重阻塞协程
     */
    fun start(config: WebHookConfig? = null): Future<WebHookHttpServer>

    /**
     * 关闭机器人
     */
    fun close(): Future<Void>

    /**
     * 机器人头像地址
     */
    val avatar: String

    /**
     * 机器人昵称
     */
    val nick: String

    /**
     * 特殊关联应用的 openid
     * 需要特殊申请并配置后才会返回
     */
    val unionOpenid: String?

    /**
     * 机器人关联的互联应用的用户信息
     * 与 union_openid 关联的应用是同一个
     */
    val unionUserAccount: String?

    /**
     * 获取用户频道列表
     */
    fun getGuilds(): Future<List<Channel>> {
        return HttpAPIClient.getGuilds(botInfo)
    }

    /**
     * 获取用户频道列表具体信息
     * 该方法区别于 getGuilds() 方法，该方法获取频道具体信息，而不是作为一个抽象的频道列表返回
     */
    fun getGuildInfos(): Future<List<GuildBean>> {
        return HttpAPIClient.getGuildInfos(botInfo)
    }


    /**
     * Bot 事件监听，监听来自于创建该 vertx 的事件监听。 但凡是在该 vertx 中传播的事件都能被捕捉
     */
    fun <T : Event> onVertxEvent(cls: Class<out T>, isUseWorkerThread: Boolean = false, callback: Consumer<T>) {
        config.apply {
            EventMapping.add(cls)
            val stackTrace = Thread.currentThread().stackTrace.getOrNull(1)?.toString() ?: ""
            val consumer = getVertxEventBus().localConsumer<T>(cls.name) { msg ->
                val scope = if (isUseWorkerThread) Dispatchers.vertxWorker(vertx) else Dispatchers.vertx(vertx)
                CoroutineScope(scope).launch {
                    kotlin.runCatching {
                        callback.accept(msg.body())
                    }.onFailure {
                        throw EventBusException(RuntimeException("Caller: $stackTrace", it))
                    }
                }
            }
            consumers.add(consumer)
        }
    }

    /**
     * Bot 事件监听，监听来自于创建该 vertx 的事件监听。 但凡是在该 vertx 中传播的事件都能被捕捉
     */
    fun <T : Event> onVertxEvent(cls: Class<out T>, callback: Consumer<T>) {
        onVertxEvent(cls, false, callback)
    }

    /**
     * Bot 事件监听，监听来自于创建该 vertx 的事件监听 并且 只监听该 Bot 的事件
     */
    fun <T : Event> onEvent(cls: Class<out T>, isUseWorkerThread: Boolean = false, callback: Consumer<T>) {
        config.apply {
            EventMapping.add(cls)
            val stackTrace = Thread.currentThread().stackTrace.getOrNull(1)?.toString() ?: ""
            val consumer = getVertxEventBus().localConsumer<T>(cls.name) { msg ->
                if (msg.body().botInfo.token.appID == this.token.appID) {
                    val scope = if (isUseWorkerThread) Dispatchers.vertxWorker(vertx) else Dispatchers.vertx(vertx)
                    CoroutineScope(scope).launch {
                        kotlin.runCatching {
                            callback.accept(msg.body())
                        }.onFailure {
                            throw EventBusException(RuntimeException("Caller: $stackTrace", it))
                        }
                    }
                }
            }
            consumers.add(consumer)
        }
    }

    /**
     * Bot 事件监听，监听来自于创建该 vertx 的事件监听 并且 只监听该 Bot 的事件
     */
    fun <T : Event> onEvent(cls: Class<out T>, callback: Consumer<T>) {
        onEvent(cls, false, callback)
    }
}

data class BotConfig(
    @Deprecated("The official has abandoned the WebSocket method")
    val intents: Int,
    val vertx: Vertx,
    @Deprecated("The official has abandoned the WebSocket method")
    val shards: BotSection,
    val consumers: HashSet<MessageConsumer<*>>,
    val token: Token,
    var reconnect: Boolean = true,
    /**
     * Bot丢失链接后的重试次数
     */
    var retry: Int = -1,
) : Serializable {

    /**
     * 适用于当前机器人的事件总线
     */
    val botEventBus: BotEventBus by lazy {
        BotEventBus(vertx.eventBus())
    }

    /**
     * 配置监听通道列表
     */
    @Deprecated("The official has abandoned the WebSocket method")
    val intentsSet: Set<Intents>
        get() = Intents.decodeIntents(intents)

    /**
     * 获取机器人所在的 EventBus
     */
    fun getVertxEventBus(): EventBus {
        return vertx.eventBus()
    }
}

class BotConfigBuilder(token0: Token? = null) {

    constructor(appid: String, token: String, appSecret: String, version: Int) : this(
        Token(
            appid,
            token,
            appSecret,
            version = version
        )
    )

    constructor(appid: String, token: String, appSecret: String) : this(Token(appid, token, appSecret))
    constructor(appid: String, token: String, version: Int) : this(Token(appid, token, version = version))
    constructor(appid: String, token: String) : this(Token(appid, token))
    constructor() : this(null)

    init {
        token0?.let { setToken(it) }
    }

    /**
     * 配置监听通道列表
     * 请使用 setIntents 方法设置
     */
    private var intents: Int = Intents.Presets.DEFAULT.code

    @Deprecated("The official has abandoned the WebSocket method")
    fun setIntents(intents0: Int): BotConfigBuilder {
        intents = intents0
        return this
    }

    @Deprecated("The official has abandoned the WebSocket method")
    fun setIntents(vararg intents0: Intents): BotConfigBuilder {
        intents = Intents.START.and(*intents0)
        return this
    }

    @Deprecated("The official has abandoned the WebSocket method")
    fun setIntents(intents0: Intents.Presets): BotConfigBuilder {
        intents = intents0.code
        return this
    }


    /**
     * 让 bot 在这个 vertx 上运行。
     * 注意如果选择自己创建 vertx 请不要直接使用 EventBus来订阅事件请使用本类中的方法订阅。如果要使用 EventBus 请使用 createVertx 方法创建集群
     */
    private var vertx: Vertx = GLOBAL_VERTX_INSTANCE

    fun setVertx(vertx0: Vertx): BotConfigBuilder {
        vertx = vertx0
        return this
    }

    /**
     * 机器人切片，vertx 会根据当前切片进行启动机器人
     */
    @Deprecated("The official has abandoned the WebSocket method")
    private var shards: BotSection = BotSection()

    @Deprecated("The official has abandoned the WebSocket method")
    fun setShards(shards0: BotSection): BotConfigBuilder {
        shards = shards0
        return this
    }

    /**
     * 监听集合
     */
    private var consumers: HashSet<MessageConsumer<*>> = HashSet()

    fun setConsumers(consumers0: HashSet<MessageConsumer<*>>): BotConfigBuilder {
        consumers = consumers0
        return this
    }

    /**
     * 对于这个机器人的鉴权系统
     */
    private var token: Token? = null
    fun setToken(token0: Token): BotConfigBuilder {
        token = token0
        return this
    }

    @JvmOverloads
    fun setToken(appid: String, token: String = "", appSecret: String = ""): BotConfigBuilder {
        this.token = Token(appid, token, appSecret)
        return this
    }


    /**
     * 是否允许重连
     */
    @Deprecated("The official has abandoned the WebSocket method")
    private var reconnect: Boolean = true

    @Deprecated("The official has abandoned the WebSocket method")
    fun setReconnect(reconnect0: Boolean): BotConfigBuilder {
        reconnect = reconnect0
        return this
    }

    /**
     * 是否允许重试
     */
    @Deprecated("The official has abandoned the WebSocket method")
    private var retry: Int = 8

    @Deprecated("The official has abandoned the WebSocket method")
    fun setRetry(retry0: Int): BotConfigBuilder {
        retry = retry0
        return this
    }

    fun build(): BotConfig = BotConfig(
        intents = intents,
        vertx = vertx,
        shards = shards,
        consumers = consumers,
        token = token ?: throw NullPointerException("token is null")
    )

    @Deprecated("")
    fun createBot(): Bot {
        return Bot.createBot(this)
    }
}

/**
 * Bot 事件监听，监听来自于创建该 vertx 的事件监听
 *
 */
@JvmOverloads
inline fun <reified T : Event> Bot.onVertxEvent(
    isUseWorkerThread: Boolean = false,
    crossinline callback: suspend Message<T>.(message: T) -> Unit
) {
    this.config.apply {
        EventMapping.add(T::class.java)
        val stackTrace = Thread.currentThread().stackTrace.getOrNull(1)?.toString() ?: ""
        val consumer = getVertxEventBus().localConsumer<T>(T::class.java.name) { msg ->
            val scope = if (isUseWorkerThread) Dispatchers.vertxWorker(vertx) else Dispatchers.vertx(vertx)
            CoroutineScope(scope).launch {
                kotlin.runCatching {
                    msg.callback(msg.body())
                }.onFailure {
                    throw EventBusException(RuntimeException("Caller: $stackTrace", it))
                }
            }
        }
        consumers.add(consumer)
    }
}

/**
 * Bot 事件监听，监听来自于创建该 vertx 的事件监听 并且 只监听该 Bot 的事件
 *
 */
@JvmOverloads
inline fun <reified T : Event> Bot.onEvent(
    isUseWorkerThread: Boolean = false,
    crossinline callback: suspend Message<T>.(message: T) -> Unit
) {
    this.config.apply {
        EventMapping.add(T::class.java)
        val stackTrace = Thread.currentThread().stackTrace.getOrNull(1)?.toString() ?: ""
        val consumer = getVertxEventBus().localConsumer<T>(T::class.java.name) { msg ->
            if (msg.body().botInfo.token.appID == this.token.appID) {
                val scope = if (isUseWorkerThread) Dispatchers.vertxWorker(vertx) else Dispatchers.vertx(vertx)
                CoroutineScope(scope).launch {
                    kotlin.runCatching {
                        callback(msg, msg.body())
                    }.onFailure {
                        throw EventBusException(RuntimeException("Caller: $stackTrace", it))
                    }
                }
            }
        }
        consumers.add(consumer)
    }
}
