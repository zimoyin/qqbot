package com.github.zimoyin.qqbot.net.http.api

import com.github.zimoyin.qqbot.net.http.DefaultHttpClient
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.HttpRequest
import kotlin.reflect.KProperty

/**
 * API 列表，列出了所有的API HTTP 请求客户端，可以通过他来装填参数与访问
 * 如果只是想直接访问获取API的数据，可以直接使用 HttpAPIClient.xxx() 来发送请求和处理响应
 */

object API {
    /**
     * 网关: 获取通用 WSS 接入点
     */
    val Gateway: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.get("/gateway")
    }

    /**
     * 网关: 获取通用 WSS 接入点。与切片建议信息
     */
    val GatewayV2: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.get("/gateway/bot")
    }

    /**
     * 动态令牌: 获取动态令牌
     *
     * 请求参数(JSON)
     * 属性	        类型	    必填	    说明
     * appId	    string	是	    在开放平台管理端上获得。
     * clientSecret	string	是	    在开放平台管理端上获得。
     *
     *
     * 返回参数(JSON)
     * 属性	        类型	    说明
     * access_token	string	获取到的凭证。
     * expires_in	number	凭证有效时间，单位：秒。目前是7200秒之内的值。
     *
     *
     * 该返回值用于鉴权（适用于的域：api.sgroup.qq.com）
     * 鉴权方式: 请求头
     * 名称	            类型	    必填	描述
     * Authorization	string	是	格式值："QQBot ACCESS_TOKEN"
     * X-Union-Appid	string	是	格式值："BOT_APPID", 机器人 appid
     */
    val AccessToken: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.createPost("https://bots.qq.com/app/getAppAccessToken")
    }

    /**
     * 在频道里面发送信息
     */
    val SendChannelMessage: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.post("/channels/{channel_id}/messages")
    }

    /**
     * 在频道里面发送私信信息
     */
    val SendChannelPrivateMessage: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.post("/dms/{guild_id}/messages")
    }

    /**
     * 获取机器人信息
     */
    val BotInfo: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.get("/users/@me")
    }

    /**
     * 获取机器人加入的频道列表
     */
    val GuildList: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.get("/users/@me/guilds")
    }

    /**
     * 获取频道的详细信息
     */
    val GuildDetails: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.get("/guilds/{guild_id}")
    }


    /**
     * 获取子频道列表
     */
    val Channels: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.get("/guilds/{guild_id}/channels")
    }

    /**
     * 获取子频道详情
     */
    val ChannelDetails: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.get("/channels/{channel_id}")
    }

    /**
     * 获取频道成员列表
     */
    val GuildMembers: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.get("/guilds/{guild_id}/members")
    }

    /**
     * 获取在线频道成员数
     */
    val ChannelOnlineMemberSize: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.get("/channels/{channel_id}/online_nums")
    }

    /**
     * 获取频道身份组列表
     */
    val GuildRoles: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.get("/guilds/{guild_id}/roles")
    }

    /**
     * 获取频道身份组下的所有成员列表
     */
    val GuildRoleMembers: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.get("/guilds/{guild_id}/roles/{role_id}/members")
    }

    /**
     * 创建身份组
     */
    val CreateGuildRole: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.post("/guilds/{guild_id}/roles")
    }

    /**
     * 修改身份组
     */
    val UpdateGuildRole: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.patch("/guilds/{guild_id}/roles/{role_id}")
    }

    /**
     * 删除身份组
     */
    val DeleteGuildRole: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.delete("/guilds/{guild_id}/roles/{role_id}")
    }

    /**
     * 创建频道身份组成员
     */
    val AddGuildRoleMember: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.put("/guilds/{guild_id}/members/{user_id}/roles/{role_id}")
    }

    /**
     * 删除频道身份组成员
     */
    val DeleteGuildRoleMember: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.delete("/guilds/{guild_id}/members/{user_id}/roles/{role_id}")
    }

    /**
     * 创建新的子频道
     */
    val CreateSubChannel: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.post("/guilds/{guild_id}/channels")
    }

    /**
     * 修改子频道
     */
    val UpdateSubChannel: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.patch("/channels/{channel_id}")
    }

    /**
     * 删除子频道
     */
    val DeleteSubChannel: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.delete("/channels/{channel_id}")
    }

    /**
     * 删除频道成员
     */
    val DeleteSubChannelMember: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.delete("/guilds/{guild_id}/members/{user_id}")
    }

    /**
     * 获取频道消息频率的设置详情
     */
    val ChannelRate: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.get("/guilds/{guild_id}/message/setting")
    }
    /**
     * 频道全员禁言
     */
    val ChannelMute: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.patch("/guilds/{guild_id}/mute")
    }

    /**
     * 频道指定成员禁言
     */
    val ChannelMuteMember: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.patch("/guilds/{guild_id}/members/{user_id}/mute")
    }
    /**
     * 频道批量成员禁言
     */
    val ChannelMuteMembers: HttpRequest<Buffer> by LazyInit {
        DefaultHttpClient.client.patch("/guilds/{guild_id}/mute")
    }


    /**
     *  @param repeat 是否重复生成
     *  @param valueGenerator 值生成器
     */
    private class LazyInit(private val repeat: Boolean = false, private val valueGenerator: () -> HttpRequest<Buffer>) {
        private val client by lazy { valueGenerator() }
        val uri0: String

        init {
            uri0 = client.uri()
        }

        operator fun getValue(thisRef: Any?, property: KProperty<*>): HttpRequest<Buffer> {
            return if (repeat) {
                valueGenerator().init()
            } else {
                client.init()
            }
        }

        private fun HttpRequest<Buffer>.init(): HttpRequest<Buffer> {
            queryParams().clear()
            headers().clear()
            uri(uri0)
            return this.putHeaders(DefaultHttpClient.DefaultHeaders)
        }
    }
}