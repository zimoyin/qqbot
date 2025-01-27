package io.github.zimoyin.qqbot.bot.message.type

import io.github.zimoyin.qqbot.utils.ex.cleanJsonObject
import io.github.zimoyin.qqbot.utils.ex.toJsonObject
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
class KeyboardMessage(
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
        @JvmOverloads
        fun create(id: String? = null, content: CustomKeyboard? = null): KeyboardMessage {
            return when {
                id != null && content != null -> KeyboardMessage(
                    jsonObjectOf(
                        "id" to id, "content" to content.toJsonObject()
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
 * 自定义 km
 * @property rows 按钮行列表，每行可以包含多个按钮
 * @property style 键盘样式，例如字体大小
 */
data class CustomKeyboard(
    val rows: List<Row> = emptyList(),  // 按钮行列表，每行可以包含多个按钮
    val style: KeyboardStyle? = null  // 键盘样式，例如字体大小
) {

    /**
     * 转换为 JsonObject 并移除为 null 的字段
     */
    fun toJson(): JsonObject {
        return this.toJsonObject().cleanJsonObject()
    }

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
            val PermissionTypeManager = PermissionType(1)         // 仅频道管理者可操作
            val PermissionTypeAll = PermissionType(2)             // 所有人可操作
            val PermissionTypeSpecifyRoleIDs = PermissionType(3)  // 指定身份组可操作
        }
    }

    /**
     * 模板 ID
     * @property template_id 官方模板 ID
     * @property custom_template_id 自定义模板 ID
     */
    data class TemplateID(
        val template_id: UInt? = null,  // 官方模板 ID
        val custom_template_id: String? = null  // 自定义模板 ID
    )

    /**
     * 订阅数据
     * @property template_ids 订阅按钮对应的模板 ID 列表
     */
    data class SubscribeData(
        val template_ids: List<TemplateID>? = null  // 订阅按钮对应的模板 ID 列表
    )

    /**
     * 按钮权限配置
     * @property type 权限类型
     * @property specify_role_ids 有权限的身份组 ID 列表（仅频道可用）
     * @property specify_user_ids 有权限的用户 ID 列表
     */
    data class Permission(
        val type: Int,  // 权限类型
        val specify_role_ids: List<String>? = null,  // 有权限的身份组 ID 列表（仅频道可用）
        val specify_user_ids: List<String>? = null  // 有权限的用户 ID 列表
    )

    /**
     * 二次确认数据
     * @property content 提示文本，最多 40 个字符，不能包含 URL
     * @property confirm_text 确认按钮文字，最多 4 个字符，默认为 "确认"
     * @property cancel_text 取消按钮文字，最多 4 个字符，默认为 "取消"
     */
    data class Modal(
        val content: String? = null,  // 提示文本，最多 40 个字符，不能包含 URL
        val confirm_text: String? = null,  // 确认按钮文字，最多 4 个字符，默认为 "确认"
        val cancel_text: String? = null  // 取消按钮文字，最多 4 个字符，默认为 "取消"
    )

    /**
     * 按钮点击操作
     * @property type 操作类型
     * @property permission 按钮权限配置
     * @property click_limit 可点击的次数（已弃用，默认不限）
     * @property data 操作相关的数据
     * @property enter 指令按钮是否直接发送 data（默认 false）
     * @property at_bot_show_channel_list 弹出子频道选择器（已弃用，默认 false）
     * @property subscribe_data 订阅按钮数据，仅当 type 为 ActionTypeSubscribe 时有效
     * @property modal 二次确认操作
     */
    data class Action(
        val type: Int,  // 操作类型
        val permission: Permission? = null,  // 按钮权限配置
        val click_limit: Int = Int.MAX_VALUE,  // 可点击的次数（已弃用）
        val data: String? = null,  // 操作相关的数据
        val enter: Boolean = false,  // 指令按钮是否直接发送 data（默认 false）
        val at_bot_show_channel_list: Boolean = false,  // 弹出子频道选择器（已弃用）
        val subscribe_data: SubscribeData? = null,  // 订阅按钮数据
        val modal: Modal? = null,  // 二次确认操作
        val unsupport_tips: String? = null  // 客户端不支持时的提示文案
    )

    /**
     * 按钮渲染数据
     * @property label 按钮上的文字
     * @property visited_label 点击后按钮上的文字
     * @property style 按钮样式：0 灰色线框，1 蓝色线框
     */
    data class RenderData(
        val label: String? = null,  // 按钮上的文字
        val visited_label: String? = null,  // 点击后按钮上的文字
        val style: Int? = null  // 按钮样式：0 灰色线框，1 蓝色线框
    )

    /**
     * 按钮数据
     * @property id 按钮 ID，在同一个 keyboard 消息内唯一
     * @property render_data 按钮渲染数据
     * @property action 按钮点击操作相关字段
     * @property group_id 按钮分组 ID，仅当 action.type 为 ActionTypeCallback 时有效
     */
    data class Button(
        val id: String,  // 按钮 ID
        val render_data: RenderData? = null,  // 按钮渲染数据
        val action: Action? = null,  // 按钮点击操作相关字段
        val group_id: String? = null  // 按钮分组 ID（仅当 action.type 为 ActionTypeCallback 时有效）
    )

    /**
     * 按钮行
     * @property buttons 行内的按钮列表
     */
    data class Row(
        val buttons: List<Button>? = null  // 行内的按钮列表
    )

    /**
     * 键盘样式
     * @property font_size 字体大小
     */
    data class KeyboardStyle(
        val font_size: String? = null  // 字体大小
    )
}


/**
 * 自定义 KeyboardMessage DSL
 */
class CustomKeyboardBuilder {
    private val rows = mutableListOf<CustomKeyboard.Row>()

    var style: CustomKeyboard.KeyboardStyle? = null

    fun row(block: RowsBuilder.() -> Unit) {
        rows.addAll(RowsBuilder().apply(block).build())
    }

    fun build(): CustomKeyboard = CustomKeyboard(rows, style)

    class RowsBuilder {
        private val rows = mutableListOf<CustomKeyboard.Row>()

        @Deprecated("please use button")
        fun buttons(block: ButtonsBuilder.() -> Unit) {
            rows.add(CustomKeyboard.Row(ButtonsBuilder().apply(block).build()))
        }

        fun button(block: ButtonBuilder.() -> Unit) {
            buttons {
                button(block)
            }
        }

        fun build(): List<CustomKeyboard.Row> = rows
    }

    class ButtonsBuilder {
        private val buttons = mutableListOf<CustomKeyboard.Button>()

        fun button(block: ButtonBuilder.() -> Unit) {
            buttons.add(ButtonBuilder().apply(block).build())
        }

        fun build(): List<CustomKeyboard.Button> = buttons
    }

    class ButtonBuilder {
        var id: String = ""
        private var renderData: CustomKeyboard.RenderData? = null
        private var action: CustomKeyboard.Action? = null
        var groupId: String? = null

        fun renderData(block: RenderDataBuilder.() -> Unit) {
            renderData = RenderDataBuilder().apply(block).build()
        }

        fun action(block: ActionBuilder.() -> Unit) {
            action = ActionBuilder().apply(block).build()
        }

        fun build(): CustomKeyboard.Button {
            require(id.isNotEmpty()) { "Button ID must not be empty." }
            return CustomKeyboard.Button(id, renderData, action, groupId)
        }
    }

    class RenderDataBuilder {
        var label: String? = null
        var visitedLabel: String? = null
        var style: Int? = null

        fun build(): CustomKeyboard.RenderData = CustomKeyboard.RenderData(label, visitedLabel, style)
    }

    class ActionBuilder {
        var type: Int = -1
        private var permission: CustomKeyboard.Permission? = null
        var clickLimit: Int = Int.MAX_VALUE
        var data: String? = null
        var enter: Boolean = false
        var atBotShowChannelList: Boolean = false
        var unsupportTips: String = ""
        var subscribeData: CustomKeyboard.SubscribeData? = null
        private var modal: CustomKeyboard.Modal? = null

        fun permission(block: PermissionBuilder.() -> Unit) {
            permission = PermissionBuilder().apply(block).build()
        }

        fun modal(block: ModalBuilder.() -> Unit) {
            modal = ModalBuilder().apply(block).build()
        }

        fun build(): CustomKeyboard.Action {
            require(type >= 0) { "Action type must be set." }
            return CustomKeyboard.Action(
                type = type,
                permission = permission,
                click_limit = clickLimit,
                data = data,
                enter = enter,
                at_bot_show_channel_list = atBotShowChannelList,
                subscribe_data = subscribeData,
                modal = modal,
                unsupport_tips = unsupportTips
            )
        }
    }

    class PermissionBuilder {
        var type: Int = CustomKeyboard.PermissionType.PermissionTypeAll.value
        var specifyRoleIds: List<String>? = null
        var specifyUserIds: List<String>? = null

        fun build(): CustomKeyboard.Permission = CustomKeyboard.Permission(type, specifyRoleIds, specifyUserIds)
    }

    class ModalBuilder {
        var content: String? = null
        var confirmText: String? = null
        var cancelText: String? = null

        fun build(): CustomKeyboard.Modal = CustomKeyboard.Modal(content, confirmText, cancelText)
    }
}

fun customKeyboard(block: CustomKeyboardBuilder.() -> Unit): CustomKeyboard {
    return CustomKeyboardBuilder().apply(block).build()
}
