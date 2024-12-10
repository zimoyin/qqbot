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

data class CustomKeyboard(
    val rows: List<Row> = emptyList(),
    val style: KeyboardStyle? = null
) {
    class ActionType private constructor(val value: Int) {
        companion object {
            val ActionTypeURL = ActionType(0)           // http 或小程序客户端识别 schema，data 字段为链接
            val ActionTypeCallback = ActionType(1)     // 回调互动回调地址，data 传给互动回调地址
            val ActionTypeAtBot = ActionType(2)        // at机器人，决定在当前频道或选择频道，自动在输入框 @bot data
            val ActionTypeMQQAPI = ActionType(3)       // 客户端 native 跳转链接
            val ActionTypeSubscribe = ActionType(4)    // 订阅按钮
        }
    }

    class PermissionType private constructor(val value: Int) {
        companion object {
            val PermissionTypeSpecifyUserIDs = PermissionType(0)  // 仅指定这条消息的人可操作
            val PermissionTypManager = PermissionType(1)         // 仅频道管理者可操作
            val PermissionTypAll = PermissionType(2)             // 所有人可操作
            val PermissionTypSpecifyRoleIDs = PermissionType(3)  // 指定身份组可操作
        }

    }

    data class TemplateID(
        val templateId: UInt? = null, // 官方模板 ID
        val customTemplateId: String? = null // 自定义模板 ID
    )

    data class SubscribeData(
        val templateIds: List<TemplateID>? = null // 订阅按钮对应的模板 ID 列表
    )


    data class Permission(
        val type: Int,
        val specifyRoleIds: List<String>? = null,
        val specifyUserIds: List<String>? = null
    )

    data class Modal(
        val content: String? = null, // 二次确认的提示文本
        val confirmText: String? = null, // 确认按钮文字
        val cancelText: String? = null // 取消按钮文字
    )

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

    data class RenderData(
        val label: String? = null,
        val visitedLabel: String? = null,
        val style: Int? = null // 按钮样式
    )

    data class Button(
        val id: String,
        val renderData: RenderData? = null,
        val action: Action? = null,
        val groupId: String? = null
    )

    data class Row(
        val buttons: List<Button>? = null
    )

    data class KeyboardStyle(
        val fontSize: String? = null
    )
}
