package com.github.zimoyin.qqbot.utils.ex

import com.github.zimoyin.qqbot.utils.JSON
import io.vertx.core.json.JsonObject


fun Any.toJsonObject(): JsonObject {
    return when (this) {
        is JsonObject -> return this
        is String -> JSON.toJsonObject(this)
        else -> JSON.toJsonObject(this)
    }
}
