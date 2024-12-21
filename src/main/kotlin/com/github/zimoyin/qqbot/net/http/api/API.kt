package com.github.zimoyin.qqbot.net.http.api


import com.github.zimoyin.qqbot.LocalLogger
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.HttpRequest
import org.slf4j.LoggerFactory
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
        TencentOpenApiHttpClient.client.get("/gateway")
    }

    /**
     * 网关: 获取通用 WSS 接入点。与切片建议信息
     */
    val GatewayV2: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.get("/gateway/bot")
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
        TencentOpenApiHttpClient.client.post("/app/getAppAccessToken")
            .host("bots.qq.com")
    }

    /**
     * 给人发送信息
     */
    val SendFriendMessage: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.post("/v2/users/{openid}/messages")
    }


    /**
     * 在群组里面发送信息
     */
    val SendGroupMessage: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.post("/v2/groups/{group_openid}/messages")
    }

    /**
     * 在频道里面发送信息
     */
    val SendChannelMessage: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.post("/channels/{channel_id}/messages")
    }

    /**
     * 在频道里面发送私信信息
     */
    val SendChannelPrivateMessage: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.post("/dms/{guild_id}/messages")
    }

    /**
     * 上传群组媒体资源
     */
    val uploadGroupMediaResource: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.post("/v2/groups/{group_openid}/files")
    }


    /**
     * 上传好友媒体资源
     */
    val uploadFriendMediaResource: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.post("/v2/users/{openid}/files")
    }

    /**
     * 获取机器人信息
     */
    val BotInfo: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.get("/users/@me")
    }

    /**
     * 获取机器人加入的频道列表
     */
    val GuildList: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.get("/users/@me/guilds")
    }

    /**
     * 获取频道的详细信息
     */
    val GuildDetails: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.get("/guilds/{guild_id}")
    }


    /**
     * 获取子频道列表
     */
    val Channels: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.get("/guilds/{guild_id}/channels")
    }

    /**
     * 获取子频道详情
     */
    val ChannelDetails: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.get("/channels/{channel_id}")
    }

    /**
     * 获取频道成员列表
     */
    val GuildMembers: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.get("/guilds/{guild_id}/members")
    }

    /**
     * 获取在线频道成员数
     */
    val ChannelOnlineMemberSize: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.get("/channels/{channel_id}/online_nums")
    }

    /**
     * 获取频道身份组列表
     */
    val GuildRoles: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.get("/guilds/{guild_id}/roles")
    }

    /**
     * 获取频道身份组下的所有成员列表
     */
    val GuildRoleMembers: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.get("/guilds/{guild_id}/roles/{role_id}/members")
    }

    /**
     * 创建身份组
     */
    val CreateGuildRole: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.post("/guilds/{guild_id}/roles")
    }

    /**
     * 修改身份组
     */
    val UpdateGuildRole: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.patch("/guilds/{guild_id}/roles/{role_id}")
    }

    /**
     * 删除身份组
     */
    val DeleteGuildRole: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.delete("/guilds/{guild_id}/roles/{role_id}")
    }

    /**
     * 创建频道身份组成员
     */
    val AddGuildRoleMember: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.put("/guilds/{guild_id}/members/{user_id}/roles/{role_id}")
    }

    /**
     * 删除频道身份组成员
     */
    val DeleteGuildRoleMember: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.delete("/guilds/{guild_id}/members/{user_id}/roles/{role_id}")
    }

    /**
     * 创建新的子频道
     */
    val CreateSubChannel: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.post("/guilds/{guild_id}/channels")
    }

    /**
     * 修改子频道
     */
    val UpdateSubChannel: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.patch("/channels/{channel_id}")
    }

    /**
     * 删除子频道
     */
    val DeleteSubChannel: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.delete("/channels/{channel_id}")
    }

    /**
     * 删除频道成员
     */
    val DeleteSubChannelMember: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.delete("/guilds/{guild_id}/members/{user_id}")
    }

    /**
     * 获取频道消息频率的设置详情
     */
    val ChannelRate: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.get("/guilds/{guild_id}/message/setting")
    }

    /**
     * 频道全员禁言
     */
    val ChannelMute: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.patch("/guilds/{guild_id}/mute")
    }

    /**
     * 频道指定成员禁言
     */
    val ChannelMuteMember: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.patch("/guilds/{guild_id}/members/{user_id}/mute")
    }

    /**
     * 频道批量成员禁言
     */
    val ChannelMuteMembers: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.patch("/guilds/{guild_id}/mute")
    }

    /**
     * 获取子频道用户权限
     */
    val GetChannelPermissions: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.get("/channels/{channel_id}/members/{user_id}/permissions")
    }

    /**
     * 获取子频道身份组权限
     */
    val GetChannelRolePermissions: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.get("/channels/{channel_id}/roles/{role_id}/permissions")
    }

    /**
     * 修改子频道用户权限
     */
    val UpdateChannelPermissions: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.put("/channels/{channel_id}/members/{user_id}/permissions")
    }


    /**
     * 修改子频道身份组权限
     */
    val UpdateChannelRolePermissions: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.put("/channels/{channel_id}/roles/{role_id}/permissions")
    }

    /**
     * 修改子频道身份组权限
     */
    val GetChannelBotPermissions: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.get("/guilds/{guild_id}/api_permission")
    }


    /**
     * 请求子频道身份组权限
     */
    val DemandChannelBotPermissions: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.post("/guilds/{guild_id}/api_permission/demand")
    }


    /**
     * 创建频道公告
     */
    val CreateChannelAnnouncement: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.post("/guilds/{guild_id}/announces")
    }

    /**
     * 删除频道公告
     */
    val DeleteChannelAnnouncement: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.delete("/guilds/{guild_id}/announces/{message_id}")
    }


    /**
     * 添加精华消息
     * TODO 频道事件也需要该方法
     */
    val AddEssentialMessage: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.put("/channels/{channel_id}/pins/{message_id}")
    }


    /**
     * 删除精华消息
     */
    val DeleteEssentialMessage: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.delete("/channels/{channel_id}/pins/{message_id}")
    }


    /**
     * 获取精华消息列表
     * 返回 信息ID 列表
     */
    val EssentialMessages: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.get("/channels/{channel_id}/pins")
    }


    /**
     * 获取频道日程列表
     */
    val ChannelSchedules: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.get("/channels/{channel_id}/schedules")
    }

    /**
     * 获取日程详情
     */
    val ChannelScheduleDetail: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.get("/channels/{channel_id}/schedules/{schedule_id}")
    }

    /**
     * 创建日程详情
     */
    val CreateChannelSchedule: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.post("/channels/{channel_id}/schedules")
    }

    /**
     * 修改日程详情
     */
    val UpdateChannelSchedule: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.patch("/channels/{channel_id}/schedules/{schedule_id}")
    }

    /**
     * 删除日程详情
     */
    val DeleteChannelSchedule: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.delete("/channels/{channel_id}/schedules/{schedule_id}")
    }

    /**
     * 音频控制
     * TODO 低优先级
     */
    val AudioControl: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.post("/channels/{channel_id}/audio")
    }


    /**
     * 机器人上麦
     */
    val RobotOnStage: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.put("/channels/{channel_id}/mic")
    }


    /**
     * 机器人下麦
     */
    val RobotOffStage: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.delete("/channels/{channel_id}/mic")
    }

    /**
     * 获取帖子列表
     */
    val PostList: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.get("/channels/{channel_id}/threads")
    }

    /**
     * 获取帖子详情
     */
    val PostDetail: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.get("/channels/{channel_id}/threads/{thread_id}")
    }


    /**
     * 发布帖子
     */
    val PublishPost: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.put("/channels/{channel_id}/threads")
    }


    /**
     * 删除帖子
     */
    val DeletePost: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.put("/channels/{channel_id}/threads/{thread_id}")
    }

    /**
     * 添加表情
     */
    val AddEmoji: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.put("/channels/{channel_id}/messages/{message_id}/reactions/{type}/{id}")
    }

    /**
     * 删除表情
     */
    val DeleteEmoji: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.delete("/channels/{channel_id}/messages/{message_id}/reactions/{type}/{id}")
    }

    /**
     * 撤回子频道的信息
     */
    val RecallChannelMessage: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.delete("/channels/{channel_id}/messages/{message_id}")
    }

    /**
     * 撤回频道私信的信息中我的信息
     */
    val RecallChannelMyPrivateMessage: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.delete("/dms/{guild_id}/messages/{message_id}")
    }

    /**
     * 获取消息表情表态的用户列表
     */
    val GetEmojiUserList: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.get("/channels/{channel_id}/messages/{message_id}/reactions/{type}/{id}")
    }

    /**
     * 撤回朋友聊天时的我的信息
     */
    val RecallFriendMyMessage: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.delete("/v2/users/{openid}/messages/{message_id}")
    }

    /**
     * 用于撤回机器人发送在当前群 group_openid 的消息 message_id，发送超出2分钟的消息不可撤回
     */
    val RecallGroupMyMessage: HttpRequest<Buffer> by LazyInit {
        TencentOpenApiHttpClient.client.delete("/v2/users/{openid}/messages/{message_id}")
    }

    private val logger = LocalLogger(API::class.java)

    /**
     *  @param repeat 是否重复生成
     *  @param valueGenerator 值生成器
     */
    private class LazyInit(private val repeat: Boolean = false, private val valueGenerator: () -> HttpRequest<Buffer>) {
        private val client by lazy { valueGenerator() }
        val uri0: String = client.uri()

        operator fun getValue(thisRef: Any?, property: KProperty<*>): HttpRequest<Buffer> {
            val request = if (repeat) {
                valueGenerator().init()
            } else {
                client.init()
            }
            if (isDebug) logger.debug("Http Request[${request.method()}] ${request.uri()}")
            return request
        }

        private fun HttpRequest<Buffer>.init(): HttpRequest<Buffer> {
            queryParams().clear()
            headers().clear()
            uri(uri0)
            return this.putHeaders(TencentOpenApiHttpClient.DefaultHeaders)
        }
    }

    @JvmStatic
    var isDebug = false
}
