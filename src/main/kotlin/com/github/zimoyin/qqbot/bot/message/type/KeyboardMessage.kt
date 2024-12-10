package com.github.zimoyin.qqbot.bot.message.type

import com.github.zimoyin.qqbot.utils.ex.toJsonObject
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.jsonObjectOf
import org.intellij.lang.annotations.Language

/**
 *
 * @author : zimo
 * @date : 2024/01/27
 *
 * TODO 后续支持 keyboard 对象
 */
data class KeyboardMessage(
    @Language("json") val keyboard: String,
) : MessageItem {


    companion object {
        @JvmStatic
        fun createByID(id: String): KeyboardMessage {
            return KeyboardMessage(jsonObjectOf("id" to id).encode())
        }

        @JvmStatic
        fun createByContent(@Language("json") content: String): KeyboardMessage {
            return KeyboardMessage(jsonObjectOf("content" to content.toJsonObject()).encode())
        }

        @JvmStatic
        fun createByContent(content: JsonObject): KeyboardMessage {
            return KeyboardMessage(jsonObjectOf("content" to content).encode())
        }

        @JvmStatic
        fun createByContent(content: CustomKeyboard): KeyboardMessage {
            return KeyboardMessage(jsonObjectOf("content" to content.toJsonObject()).encode())
        }


        @JvmStatic
        fun create(id: String? = null, content: CustomKeyboard? = null): KeyboardMessage {
            return when {
                id != null && content != null -> KeyboardMessage(
                    jsonObjectOf(
                        "id" to id,
                        "content" to content.toJsonObject()
                    ).encode()
                )
                id != null -> createByID(id)
                content != null -> createByContent(content.toJsonObject().encode())
                else -> throw IllegalArgumentException("parameter cannot be null")
            }
        }
    }

    override fun toString(): String {
        return keyboard
    }
}
/**
 * 自定义键盘
 * @property rows 按钮行列表，每行可以包含多个按钮
 * @property style 键盘样式，例如字体大小
 */
data class CustomKeyboard(
    val rows: List<Row> = emptyList(),
    val style: KeyboardStyle? = null
) {
    /**
     * 操作类型
     * @property value 操作类型的值
     */
    class ActionType private constructor(val value: Int) {
        companion object {
            val ActionTypeURL = ActionType(0)           // 跳转按钮：http 或小程序客户端识别 schema
            val ActionTypeCallback = ActionType(1)     // 回调按钮：回调后台接口
            val ActionTypeAtBot = ActionType(2)        // 指令按钮：自动在输入框插入 @bot data
            val ActionTypeMQQAPI = ActionType(3)       // 客户端 native 跳转链接
            val ActionTypeSubscribe = ActionType(4)    // 订阅按钮
        }
    }

    /**
     * 权限类型
     * @property value 权限类型的值
     */
    class PermissionType private constructor(val value: Int) {
        companion object {
            val PermissionTypeSpecifyUserIDs = PermissionType(0)  // 仅指定用户可操作
            val PermissionTypManager = PermissionType(1)         // 仅频道管理者可操作
            val PermissionTypAll = PermissionType(2)             // 所有人可操作
            val PermissionTypSpecifyRoleIDs = PermissionType(3)  // 指定身份组可操作
        }
    }

    /**
     * 模板 ID
     * @property templateId 官方模板 ID
     * @property customTemplateId 自定义模板 ID
     */
    data class TemplateID(
        val templateId: UInt? = null,
        val customTemplateId: String? = null
    )

    /**
     * 订阅数据
     * @property templateIds 订阅按钮对应的模板 ID 列表
     */
    data class SubscribeData(
        val templateIds: List<TemplateID>? = null
    )

    /**
     * 按钮权限配置
     * @property type 权限类型
     * @property specifyRoleIds 有权限的身份组 ID 列表（仅频道可用）
     * @property specifyUserIds 有权限的用户 ID 列表
     */
    data class Permission(
        val type: Int,
        val specifyRoleIds: List<String>? = null,
        val specifyUserIds: List<String>? = null
    )

    /**
     * 二次确认数据
     * @property content 提示文本，最多 40 个字符，不能包含 URL
     * @property confirmText 确认按钮文字，最多 4 个字符，默认为 "确认"
     * @property cancelText 取消按钮文字，最多 4 个字符，默认为 "取消"
     */
    data class Modal(
        val content: String? = null,
        val confirmText: String? = null,
        val cancelText: String? = null
    )

    /**
     * 按钮点击操作
     * @property type 操作类型
     * @property permission 按钮权限配置
     * @property clickLimit 可点击的次数（已弃用，默认不限）
     * @property data 操作相关的数据
     * @property enter 指令按钮是否直接发送 data（默认 false）
     * @property atBotShowChannelList 弹出子频道选择器（已弃用，默认 false）
     * @property subscribeData 订阅按钮数据，仅当 type 为 ActionTypeSubscribe 时有效
     * @property modal 二次确认操作
     */
    data class Action(
        val type: Int,
        val permission: Permission? = null,
        val clickLimit: UInt = UInt.MAX_VALUE,
        val data: String? = null,
        val enter: Boolean = false,
        val atBotShowChannelList: Boolean = false,
        val subscribeData: SubscribeData? = null,
        val modal: Modal? = null
    )

    /**
     * 按钮渲染数据
     * @property label 按钮上的文字
     * @property visitedLabel 点击后按钮上的文字
     * @property style 按钮样式：0 灰色线框，1 蓝色线框
     */
    data class RenderData(
        val label: String? = null,
        val visitedLabel: String? = null,
        val style: Int? = null
    )

    /**
     * 按钮数据
     * @property id 按钮 ID，在同一个 keyboard 消息内唯一
     * @property renderData 按钮渲染数据
     * @property action 按钮点击操作相关字段
     * @property groupId 按钮分组 ID，仅当 action.type 为 ActionTypeCallback 时有效
     */
    data class Button(
        val id: String,
        val renderData: RenderData? = null,
        val action: Action? = null,
        val groupId: String? = null
    )

    /**
     * 按钮行
     * @property buttons 行内的按钮列表
     */
    data class Row(
        val buttons: List<Button>? = null
    )

    /**
     * 键盘样式
     * @property fontSize 字体大小
     */
    data class KeyboardStyle(
        val fontSize: String? = null
    )
}
