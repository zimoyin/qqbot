package com.github.zimoyin.qqbot.utils

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.github.zimoyin.qqbot.utils.ex.mapTo
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.intellij.lang.annotations.Language


typealias JAny = JsonNode

/**
 * json 的注解如下:
 *  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
 *  @JsonAlias({"id", "json_id"})
 *  @JsonIgnore
 *  @JsonProperty("json_id")
 *  @JsonIgnoreProperties(ignoreUnknown = true)
 *
 *  注意事项:
 *  1. json 转为 对象的类，必须有空构造方法。如果是 data class 则必须有默认值
 *  2. 如果某个字段既可以是json，字符串.. 那么就使用 JAny 这个类型。而不是 String 来接收，防止报错
 *  3. JAny 定义的参数如何构建，请查看 String.toJAny()/JSON.xxx/JsonObject.toJAny()
 */
object JSON {

    fun newJsonArray(): JsonArray {
        return JsonArray()
    }

    fun newJsonObject(): JsonObject {
        return JsonObject()
    }

    fun toJsonArray(@Language("JSON") str: String): JsonArray {
        return JsonArray(str)
    }

    fun toJsonArray(obj: List<Any>): JsonArray {
        return JsonArray(obj)
    }

    fun toJsonArray(vararg obj: Any): JsonArray {
        return JsonArray.of(obj)
    }

    fun toJsonObject(@Language("JSON") str: String): JsonObject {
        return JsonObject(str)
    }

    fun toJsonObject(obj: Any): JsonObject {
        return JsonObject.mapFrom(obj)
    }

    fun toJsonString(obj: Any): String {
        return JsonObject.mapFrom(obj).encode()
    }

    inline fun <reified T : Any> toObject(json: JsonObject): T {
        return json.mapTo(T::class.java)
    }


    inline fun <reified T : Any> toObject(@Language("JSON") json: String): T {
        return toJsonObject(json).mapTo(T::class.java)
    }


    fun toJAny(str: String): JsonNode {
        return ObjectMapper().readTree(""""$str"""")
    }

    fun toJAny(json: JsonObject): JsonNode {
        return ObjectMapper().readTree(json.encode())
    }

    fun toJAny(json: JsonArray): JsonNode {
        return ObjectMapper().readTree(json.encode())
    }

    fun createJAny(): ObjectNode {
        return ObjectMapper().createObjectNode()
    }
}


inline fun <reified T : Any> JsonArray.mapTo(): List<T> {
    return this.map { it.toString() }.map { it.mapTo<T>() }
}
