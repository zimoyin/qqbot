package com.github.zimoyin.qqbot.bot

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Serializable

/**
 *
 * @author : zimo
 * @date : 2023/12/08
 *
 * 机器人上下文数据类，用于存储和操作机器人执行过程中的数据
 */
class BotContent : Serializable {
    /**
     * 存储机器人上下文数据,该字段是内部使用字段，不建议外部使用
     */
    val contentInternal: HashMap<String, Any?> = HashMap()

    /**
     * 记录键值对设置的位置。只记录最后一次的设置位置。
     */
    private val contextSettingRecord: HashMap<String, StackTraceElement> = HashMap()

    private val logger: Logger by lazy { LoggerFactory.getLogger(BotContent::class.java) }

    /**
     * 设置指定键的值。如果值为 null，则记录警告日志。
     *
     * @param key 要设置的键。
     * @param value 要设置的值。
     */
    operator fun set(key: String, value: Any?): Any? {
        return internalSet(key, value)
    }

    private fun internalSet(key: String, value: Any?): Any? {
        contentInternal[key] = value
        var callerFrame: StackTraceElement? = null
        var index = 3
        while (callerFrame == null && index >= 0) {
            callerFrame = Thread.currentThread().stackTrace[index]
            index--
        }
        if (callerFrame != null) contextSettingRecord[key] = callerFrame
        if (value == null) logger.warn("The value of the key value pair you are setting in the robot context is null: key -> $key")
        return value
    }

    /**
     * 获取指定键的设置记录。通常用于查找该上下文设置的位置用于debug
     *
     * @param key 要查询的键。
     * @return 指定键的设置记录，可能为 null。
     */
    fun getRecord(key: String): StackTraceElement? {
        return contextSettingRecord[key]
    }

    /**
     * 获取指定键的值，如果类型匹配则返回值，否则返回 null。
     *
     * @param key 要获取值的键。
     * @return 指定键的值，如果类型匹配则为该类型的值，否则为 null。
     */
    inline operator fun <reified T : Any> get(key: String): T? {
        if (contentInternal[key] is T) {
            return contentInternal[key] as T
        }
        return null
    }


    /**
     * 获取指定键的值，如果类型匹配则返回值，否则返回 抛出异常
     *
     * @param key 要获取值的键。
     * @return 指定键的值，如果类型匹配则为该类型的值，否则为 null。
     * @throws IllegalArgumentException 如果指定键的值不是指定类型或者不存在，则抛出异常
     */
    inline fun <reified T : Any> getValue(key: String): T {
        return get<T>(key)
            ?: throw IllegalArgumentException("Key[$key] in robot context is not a Class<${T::class.java.name}> type")
    }

    /**
     * 获取指定键的值，如果类型匹配则返回值，否则返回默认值。
     * 泛型可以不用写，编译器会自行推断
     */
    inline fun <reified T : Any> getOrDefault(key: String, default: T): T {
        return get<T>(key) ?: default
    }

    /**
     * 获取指定键的值，如果类型匹配则返回值，否则返回默认值并将该值设置到指定键中。
     * 泛型可以不用写，编译器会自行推断
     */
    inline fun <reified T : Any> getOrPut(key: String, default: T): T {
        return get<T>(key) ?: set(key, default).let { default }
    }

    fun setString(key: String, value: String): Unit {
        internalSet(key, value)
    }

    fun setInt(key: String, value: Int): Unit {
        internalSet(key, value)
    }

    fun setLong(key: String, value: Long): Unit {
        internalSet(key, value)
    }

    fun setDouble(key: String, value: Double): Unit {
        internalSet(key, value)
    }

    fun setBoolean(key: String, value: Boolean): Unit {
        internalSet(key, value)
    }

    fun setJsonArray(key: String, value: JsonArray): Unit {
        internalSet(key, value)
    }

    fun setJsonObject(key: String, value: JsonObject): Unit {
        internalSet(key, value)
    }


    fun getString(key: String): String? {
        return get<String>(key)
    }

    fun getStringValue(key: String): String {
        return getValue<String>(key)
    }

    fun getInt(key: String): Int? {
        return get<Int>(key)
    }

    fun getIntValue(key: String): Int {
        return getValue<Int>(key)
    }

    fun getLong(key: String): Long? {
        return get<Long>(key)
    }

    fun getLongValue(key: String): Long {
        return getValue<Long>(key)
    }

    fun getDouble(key: String): Double? {
        return get<Double>(key)
    }

    fun getDoubleValue(key: String): Double {
        return getValue<Double>(key)
    }

    fun getBoolean(key: String): Boolean? {
        return get<Boolean>(key)
    }

    fun getBooleanValue(key: String): Boolean {
        return getValue<Boolean>(key)
    }

    fun getJsonArray(key: String): JsonArray? {
        return get<JsonArray>(key)
    }

    fun getJsonObject(key: String): JsonObject? {
        return get<JsonObject>(key)
    }

    fun getObject(key: String): Any? {
        return get<Any>(key)
    }

    fun getObjectValue(key: String): Any {
        return getValue<Any>(key)
    }

    @JvmName("get")
    fun <T : Any> getObject(key: String, clazz: Class<T>): T? {
        val value = get<Any>(key) ?: return null
        if (clazz.isAssignableFrom(value.javaClass)) {
            return value as T
        }
        throw IllegalArgumentException("The Value of this Key[$key] is not a Class<${clazz.name}>type in the context")
    }

    @JvmName("getValue")
    fun <T : Any> getObjectValue(key: String, clazz: Class<T>): T {
        val value = get<Any>(key) ?: throw NullPointerException("The Value of this Key[$key] is null in the context")
        if (clazz.isAssignableFrom(value.javaClass)) {
            return value as T
        }
        throw IllegalArgumentException("The Value of this Key[$key] is not a Class<${clazz.name}>type in the context")
    }

    @Deprecated("不推荐")
    fun getArray(key: String): List<Any>? {
        return get<List<Any>>(key)
    }

    @Deprecated("不推荐")
    fun getArrayValue(key: String): List<Any> {
        return getValue<List<Any>>(key)
    }

    @Deprecated("不推荐")
    fun getMap(key: String): Map<String, Any>? {
        return get<Map<String, Any>>(key)
    }

    @Deprecated("不推荐")
    fun getMapValue(key: String): Map<String, Any> {
        return getValue<Map<String, Any>>(key)
    }

    fun remove(key: String) {
        contentInternal.remove(key)
        contextSettingRecord.remove(key)
    }

    fun clear() {
        contentInternal.clear()
    }

    /**
     * 遍历所有键值对并执行指定的操作。
     *
     * @param consumer 操作每个键值对的函数。
     */
    fun foreach(consumer: (String, Any?) -> Unit) {
        contentInternal.forEach { consumer(it.key, it.value) }
    }

    /**
     * 遍历所有键值对并执行指定的操作，包括设置记录。
     *
     * @param consumer 操作每个键值对的函数，包括设置记录。
     */
    fun foreach(consumer: (String, Any?, StackTraceElement?) -> Unit) {
        contentInternal.forEach { consumer(it.key, it.value, contextSettingRecord[it.key]) }
    }

    fun contains(key: String): Boolean {
        return contentInternal.containsKey(key = key)
    }

    override fun toString(): String {
        return contentInternal.toString()
    }
}
