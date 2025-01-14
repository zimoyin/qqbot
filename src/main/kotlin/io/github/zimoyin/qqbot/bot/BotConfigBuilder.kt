package io.github.zimoyin.qqbot.bot

import io.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import io.github.zimoyin.qqbot.net.Intents
import io.github.zimoyin.qqbot.net.Token
import io.vertx.core.Vertx
import io.vertx.core.eventbus.MessageConsumer

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
     * 机器人切片，vertx 会根据当前切片进行启动机器人
     */
    @Deprecated("The official has abandoned the WebSocket method")
    private var shards: BotSection = BotSection()

    @Deprecated("The official has abandoned the WebSocket method")
    fun setShards(shards0: BotSection): BotConfigBuilder {
        shards = shards0
        return this
    }


    @Deprecated("Not used")
    fun setConsumers(consumers0: HashSet<MessageConsumer<*>>): BotConfigBuilder {
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
    private var reconnect: Boolean = true

    fun setReconnect(reconnect0: Boolean): BotConfigBuilder {
        reconnect = reconnect0
        return this
    }

    /**
     * 是否允许重试
     */
    private var retry: Int = -1

    fun setRetry(retry0: Int): BotConfigBuilder {
        retry = retry0
        return this
    }

    fun build(): BotConfig = BotConfig(
        intents = intents,
        vertx = vertx,
        shards = shards,
        token = token ?: throw NullPointerException("token is null"),
        retry = retry,
        reconnect = reconnect
    )

    @Deprecated("")
    fun createBot(): Bot {
        return Bot.createBot(this)
    }
}
