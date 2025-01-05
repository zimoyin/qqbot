package io.github.zimoyin.ra3.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * LocalDateTime序列化器
 * 解决不支持 LocalDateTime
 * 使用方式为在字段上方使用 @JsonSerialize(using = LocalDateTimeSerializer::class)
 *
 *     @get:JsonSerialize(using = LocalDateTimeSerializer::class)
 *     var createTime: LocalDateTime = LocalDateTime.now(),
 */
class LocalDateTimeSerializer : JsonSerializer<LocalDateTime>() {
    override fun serialize(value: LocalDateTime, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeString(value.toString())
    }
}

/**
 *  使用注解 @JsonDeserialize
 */
class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime?>() {
    @Throws(IOException::class)
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): LocalDateTime {
        val value: String = p.text
        return LocalDateTime.parse(value, formatter) // Use the same formatter as in the serializer for consistency
    }

    companion object {
        private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        private val minValue: LocalDateTime = LocalDateTime.MIN
        private val maxValue: LocalDateTime = LocalDateTime.MAX
        private const val serialVersionUID = 1L // Add a unique ID to the deserializer if needed
    }
}
