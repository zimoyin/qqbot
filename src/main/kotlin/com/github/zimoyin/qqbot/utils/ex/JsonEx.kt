package com.github.zimoyin.qqbot.utils.ex

import com.fasterxml.jackson.databind.JsonNode
import com.github.zimoyin.qqbot.utils.JSON
import io.vertx.core.json.JsonObject


fun JsonObject.toJAny(): JsonNode {
    return JSON.toJAny(this)
}