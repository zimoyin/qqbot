package com.github.zimoyin.qqbot.net.bean.message

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * Ark消息
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageArk(
    /**
     * Ark模板id（需要先申请）
     */
    @field:JsonProperty("template_id")
    val templateId: Int? = null,

    /**
     * Ark kv值列表
     */
    @field:JsonProperty("kv")
    val kv: List<MessageArkKv>? = null,
) : Serializable

/**
 * Ark kv值
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageArkKv(
    /**
     * Key
     */
    @field:JsonProperty("key")
    val key: String? = null,

    /**
     * Value
     */
    @field:JsonProperty("value")
    val value: String? = null,

    /**
     * Ark obj类型的列表
     */
    @field:JsonProperty("obj")
    val obj: List<MessageArkObj>? = null,
) : Serializable

/**
 * Ark obj类型
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageArkObj(
    /**
     * Ark objkv列表
     */
    @field:JsonProperty("obj_kv")
    val objKv: List<MessageArkObjKv>? = null,
) : Serializable

/**
 * Ark objkv类型
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageArkObjKv(
    /**
     * Key
     */
    @field:JsonProperty("key")
    val key: String? = null,

    /**
     * Value
     */
    @field:JsonProperty("value")
    val value: String? = null,
) : Serializable
