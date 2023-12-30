package com.github.zimoyin.qqbot.utils.ex

import com.github.zimoyin.qqbot.utils.JSON
import io.vertx.core.json.JsonObject


fun Any.toJsonObject(): JsonObject {
    return JSON.toJsonObject(this)
}