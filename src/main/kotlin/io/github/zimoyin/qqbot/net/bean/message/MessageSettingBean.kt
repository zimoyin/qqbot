package io.github.zimoyin.qqbot.net.bean.message

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 频道消息频率设置对象
 * @author : zimo
 * @date : 2023/12/20
 */
data class MessageSetting(
    /**
     * 是否允许创建私信
     */
    @JsonProperty("disable_create_dm")
    val disableCreateDm: String? = null,

    /**
     * 是否允许发送主动消息
     */
    @JsonProperty("disable_push_msg")
    val disablePushMsg: String? = null,

    /**
     * 子频道 ID 数组
     */
    @JsonProperty("channel_ids")
    val channelIds: List<String>? = null,

    /**
     * 每个子频道允许主动推送消息的最大消息条数
     */
    @JsonProperty("channel_push_max_num")
    val channelPushMaxNum: Int? = null,
) : Serializable
