package com.github.zimoyin.qqbot.utils.ex

import io.vertx.core.json.JsonArray

fun List<Any>.toJsonArray(): JsonArray {
    return JsonArray(this)
}