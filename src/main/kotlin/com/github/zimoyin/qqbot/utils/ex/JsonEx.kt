package com.github.zimoyin.qqbot.utils.ex

import com.fasterxml.jackson.databind.JsonNode
import com.github.zimoyin.qqbot.utils.JSON
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject


fun JsonObject.toJAny(): JsonNode {
    return JSON.toJAny(this)
}

fun JsonObject.cleanJsonObject(): JsonObject {
    val cleanedJsonObject = JsonObject()

    this.forEach { (key, value) ->
        when (value) {
            is JsonObject -> {
                // 如果字段是 JsonObject，递归清理
                val cleanedNestedJsonObject = value.cleanJsonObject()
                if (cleanedNestedJsonObject.isEmpty.not()) {
                    cleanedJsonObject.put(key, cleanedNestedJsonObject)
                }
            }
            is JsonArray -> {
                // 如果字段是 JsonArray，递归清理每个元素
                val cleanedJsonArray = value.cleanJsonArray()
                if (cleanedJsonArray.size() > 0) {
                    cleanedJsonObject.put(key, cleanedJsonArray)
                }
            }
            else -> {
                // 其他字段，如果不是 null，直接加入
                if (value != null) {
                    cleanedJsonObject.put(key, value)
                }
            }
        }
    }

    return cleanedJsonObject
}

fun JsonArray.cleanJsonArray(): JsonArray {
    val cleanedJsonArray = JsonArray()

    this.forEach { item ->
        when (item) {
            is JsonObject -> {
                // 如果数组元素是 JsonObject，递归清理
                val cleanedNestedJsonObject = item.cleanJsonObject()
                if (cleanedNestedJsonObject.isEmpty.not()) {
                    cleanedJsonArray.add(cleanedNestedJsonObject)
                }
            }
            is JsonArray -> {
                // 如果数组元素是 JsonArray，递归清理
                val cleanedNestedJsonArray = item.cleanJsonArray()
                if (cleanedNestedJsonArray.size() > 0) {
                    cleanedJsonArray.add(cleanedNestedJsonArray)
                }
            }
            else -> {
                // 其他字段，如果不是 null，直接加入
                if (item != null) {
                    cleanedJsonArray.add(item)
                }
            }
        }
    }

    return cleanedJsonArray
}
