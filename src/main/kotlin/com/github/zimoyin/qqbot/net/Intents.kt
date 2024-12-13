package com.github.zimoyin.qqbot.net

/**
 * 事件的 intents 是一个标记位，每一位都代表不同的事件，如果需要接收某类事件，就将该位置为 1。
 *
 */
enum class Intents(val code: Int) {
    /**
     * 订阅前提
     */
    START(0),

    /**
     * 机器人与频道的消息事件
     * 基础订阅之一频道订阅，是不需要权限的订阅
     * GUILDS (1 << 0)
     *   - GUILD_CREATE           // 当机器人加入新guild时
     *   - GUILD_UPDATE           // 当guild资料发生变更时
     *   - GUILD_DELETE           // 当机器人退出guild时
     *   - CHANNEL_CREATE         // 当channel被创建时
     *   - CHANNEL_UPDATE         // 当channel被更新时
     *   - CHANNEL_DELETE         // 当channel被删除时
     */
    GUILDS(1 shl 0),

    /**
     * 成员变更事件
     * 基础订阅之一成员订阅，是不需要权限的订阅
     * GUILD_MEMBERS (1 << 1)
     *   - GUILD_MEMBER_ADD       // 当成员加入时
     *   - GUILD_MEMBER_UPDATE    // 当成员资料变更时
     *   - GUILD_MEMBER_REMOVE    // 当成员被移除时
     */
    GUILD_MEMBERS(1 shl 1),

    /**
     * 仅私域 - 消息事件
     * GUILD_MESSAGES (1 << 9)    // 消息事件，仅私域 机器人能够设置此 intents。
     *   - MESSAGE_CREATE         // 发送消息事件，代表频道内的全部消息，而不只是 at 机器人的消息。内容与 AT_MESSAGE_CREATE 相同
     *   - MESSAGE_DELETE         // 删除（撤回）消息事件
     */
    GUILD_MESSAGES(1 shl 9),

    /**
     * 信息表情
     * GUILD_MESSAGE_REACTIONS (1 << 10)
     *   - MESSAGE_REACTION_ADD    // 为消息添加表情表态
     *   - MESSAGE_REACTION_REMOVE // 为消息删除表情表态
     */
    GUILD_MESSAGE_REACTIONS(1 shl 10),

    /**
     * 私信事件
     * DIRECT_MESSAGE (1 << 12)
     *   - DIRECT_MESSAGE_CREATE   // 当收到用户发给机器人的私信消息时
     *   - DIRECT_MESSAGE_DELETE   // 删除（撤回）消息事件
     */
    DIRECT_MESSAGE(1 shl 12),

    /**
     * 公域事件 - 论坛事件
     * OPEN_FORUMS_EVENT (1 << 18)      // 论坛事件, 此为公域的论坛事件
     *   - OPEN_FORUM_THREAD_CREATE     // 当用户创建主题时
     *   - OPEN_FORUM_THREAD_UPDATE     // 当用户更新主题时
     *   - OPEN_FORUM_THREAD_DELETE     // 当用户删除主题时
     *   - OPEN_FORUM_POST_CREATE       // 当用户创建帖子时
     *   - OPEN_FORUM_POST_DELETE       // 当用户删除帖子时
     *   - OPEN_FORUM_REPLY_CREATE      // 当用户回复评论时
     *   - OPEN_FORUM_REPLY_DELETE      // 当用户删除评论时
     */
    @Deprecated("新版API文档中被移除")
    OPEN_FORUMS_EVENT(1 shl 18),

    /**
     * 音视频/直播子频道成员进出事件
     * AUDIO_OR_LIVE_CHANNEL_MEMBER (1 << 19)
     *  - AUDIO_OR_LIVE_CHANNEL_MEMBER_ENTER  // 当用户进入音视频/直播子频道
     *  - AUDIO_OR_LIVE_CHANNEL_MEMBER_EXIT   // 当用户离开音视频/直播子频道
     */
    @Deprecated("新版API文档中被移除")
    AUDIO_OR_LIVE_CHANNEL_MEMBER(1 shl 19),

    /**
     * 群聊事件
     * GROUP_AND_C2C_EVENT
     */
//    @Deprecated("新版API文档中被移除")
    GROUP_INTENTS(1 shl 25),

    /**
     * 互动事件
     * INTERACTION (1 << 26)
     *   - INTERACTION_CREATE     // 互动事件创建时
     */
    INTERACTION(1 shl 26),

    /**
     * 信息审核事件
     * MESSAGE_AUDIT (1 << 27)
     * - MESSAGE_AUDIT_PASS     // 消息审核通过
     * - MESSAGE_AUDIT_REJECT   // 消息审核不通过
     */
    MESSAGE_AUDIT(1 shl 27),

    /**
     * 仅私域 - 论坛事件
     * FORUMS_EVENT (1 << 28)  // 论坛事件，仅 *私域* 机器人能够设置此 intents。
     *   - FORUM_THREAD_CREATE     // 当用户创建主题时
     *   - FORUM_THREAD_UPDATE     // 当用户更新主题时
     *   - FORUM_THREAD_DELETE     // 当用户删除主题时
     *   - FORUM_POST_CREATE       // 当用户创建帖子时
     *   - FORUM_POST_DELETE       // 当用户删除帖子时
     *   - FORUM_REPLY_CREATE      // 当用户回复评论时
     *   - FORUM_REPLY_DELETE      // 当用户回复评论时
     *   - FORUM_PUBLISH_AUDIT_RESULT      // 当用户发表审核通过时
     */
    FORUMS_EVENT(1 shl 28),

    /**
     * 语音事件
     * AUDIO_ACTION (1 << 29)
     *   - AUDIO_START             // 音频开始播放时
     *   - AUDIO_FINISH            // 音频播放结束时
     *   - AUDIO_ON_MIC            // 上麦时
     *   - AUDIO_OFF_MIC           // 下麦时
     */
    AUDIO_ACTION(1 shl 29),

    /**
     * 消息事件
     * 公域消息事件 基础订阅之一成员订阅，是不需要权限的订阅
     * PUBLIC_GUILD_MESSAGES (1 << 30) // 消息事件，此为公域的消息事件
     *   - AT_MESSAGE_CREATE       // 当收到@机器人的消息时
     *   - PUBLIC_MESSAGE_DELETE   // 当频道的消息被删除时
     */
    PUBLIC_GUILD_MESSAGES(1 shl 30);


    /**
     * 提前的预设
     */
    enum class Presets(val code: Int) {
        /**
         * Intents 事件订阅方式
         * 默认
         */
        DEFAULT(738726915 or INTERACTION.code),

        /**
         * 公域机器人订阅推荐
         */
        PUBLIC_INTENTS(1812468739 or INTERACTION.code ),
        PUBLIC_GROUP_INTENTS(1812468739 or GROUP_INTENTS.code or INTERACTION.code),

        /**
         * 私域机器人订阅推荐
         */
        PRIVATE_INTENTS(1007162883 or INTERACTION.code),
        PRIVATE_GROUP_INTENTS(1007162883 or GROUP_INTENTS.code);

        operator fun plus(messageAudit: Intents): Int {
            return messageAudit.and(this.code)
        }
    }

    /**
     * 使用按位或运算将多个 [Intents] 组合成一个整数代码。
     *
     * 该函数接受可变数量的 [Intents] 参数，并使用按位或运算将它们各自的代码组合成一个整数。
     * 通常用于创建一个复合代码，以同时订阅多个 Discord 事件。
     *
     * @param intents 要使用按位或运算组合的 [Intents]。
     * @return 表示组合 [Intents] 的整数代码。
     */
    fun and(vararg intents: Intents): Int {
        var code0 = code
        for (intent in intents) {
            code0 = code0 or intent.code
        }
        return code0
    }

    fun and(vararg intents: Int): Int {
        var code0 = code
        for (intent in intents) {
            code0 = code0 or intent
        }
        return code0
    }


    /**
     * 重载加法运算符，允许你使用 `Intents.xxx + Intents.xxx + Intents.xxx` 的形式。
     *
     * 该操作符会将多个 [Intents] 组合成一个整数代码，作为订阅事件的起始点。在这种形式下，可以直观地使用加法运算符来组合多个 [Intents]。
     *
     * @param messageAudit 要与其他 [Intents] 组合的 [Intents] 对象。
     * @return 表示组合 [Intents] 的整数代码，作为订阅事件的起始点。
     */
    operator fun plus(messageAudit: Intents): Int {
        return and(messageAudit)
    }

    companion object {
        /**
         * 使用按位或运算将多个 [Intents] 组合成一个整数代码，作为订阅事件的起始点。
         *
         * 该函数接受可变数量的 [Intents] 参数，并使用按位或运算将它们各自的代码组合成一个整数，
         * 作为订阅 Discord 事件的起始点。通常用于创建一个复合代码，以同时订阅多个 Discord 事件。
         *
         * @param intents 要使用按位或运算组合的 [Intents]。
         * @return 表示组合 [Intents] 的整数代码，作为订阅事件的起始点。
         */
        @JvmStatic
        fun create(vararg intents: Intents): Int {
            return START.and(*intents)
        }

        @JvmStatic
        fun create(vararg intents: Int): Int {
            return START.and(*intents)
        }

        /**
         * 解析给定的整数代码，返回对应的 [Intents] 配置项集合。
         *
         * 该方法通过按位与运算，解析整数代码并将其映射到对应的 [Intents] 配置项。起始点配置项（START）将被排除在结果集之外。
         *
         * @param code 要解析的整数代码。
         * @return 包含解析出的 [Intents] 配置项的集合。
         */
        @JvmStatic
        fun decodeIntents(code: Int): Set<Intents> {
            val result = mutableSetOf<Intents>()

            for (intent in Intents.entries) {
                if (code and intent.code == intent.code) {
                    result.add(intent)
                }
            }
            result.removeIf {
                it.code == START.code
            }

            return result
        }
    }
}

/**
 * 重载加法运算符，允许你使用 `Intents.xxx.code + Intents.DIRECT_MESSAGE` 的形式。以此作为 [Intents] 的补充
 *
 * 该操作符会将给定的整数与 [Intents] 配置项的代码进行按位或运算，以实现整数与配置项的组合。
 *
 * @param intents 要与整数进行组合的 [Intents] 配置项。
 * @return 组合后的整数代码。
 */
operator fun Int.plus(intents: Intents): Int {
    return this or intents.code
}
