package com.github.zimoyin.qqbot.net.websocket.bean

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.zimoyin.qqbot.utils.JAny
import com.github.zimoyin.qqbot.utils.JSON
import io.vertx.core.json.JsonObject



/**
 *
 * @author : zimo
 * @date : 2023/12/07
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Payload(

    /**
     * 长连接维护 op
     * 对照表:
     * CODE	    名称	                    客户端行为	        描述
     * 0	    Dispatch	            Receive	            服务端进行消息推送
     * 1	    Heartbeat	            Send/Receive	    客户端或服务端发送心跳
     * 2	    Identify	            Send	            客户端发送鉴权
     * 6	    Resume	                Send	            客户端恢复连接
     * 7	    Reconnect	            Receive	            服务端通知客户端重新连接
     * 9	    Invalid                 Session	            Receive	当 identify 或 resume 的时候，如果参数有错，服务端会返回该消息
     * 10	    Hello	                Receive	            当客户端与网关建立 ws 连接之后，网关下发的第一条消息
     * 11	    Heartbeat ACK	        Receive/Reply	    当发送心跳成功之后，就会收到该消息
     * 12	    HTTP Callback ACK	    Reply	            仅用于 http 回调模式的回包，代表机器人收到了平台推送的数据
     *
     * 客户端行为含义如下：
     * Receive 客户端接收到服务端 push 的消息
     * Send 客户端发送消息
     * Reply 客户端接收到服务端发送的消息之后的回包（HTTP 回调模式）
     */
    @field:JsonProperty("op")
    val opcode: Int = -1,

    /**
     * d 代表事件内容，不同事件类型的事件内容格式都不同，请注意识别。
     */
    @field:JsonProperty("d")
    val eventContent: JAny? = null,

    /**
     * t 代表事件类型。
     */
    @field:JsonProperty("t")
    val eventType: String? = null,


    /**
     * hid 服务器下发的信息包的唯一标识，用于在网络层面区分数据包。客户端需要再发送心跳的时候，携带客户端收到的最新的 hid
     */
    @field:JsonProperty("s")
    val hid: Long? = null,

    @field:JsonIgnore
    var metadata:String = "none",


    @field:JsonIgnore
    var appID:String? = null,
){
    fun toJsonObject(): JsonObject {
        return JSON.toJsonObject(this)
    }

    fun toJsonString(): String {
        return JSON.toJsonString(this)
    }

    override fun toString(): String {
        return this.toJsonString()
    }
}

