package com.github.zimoyin.qqbot.bot

/**
 *
 * @author : zimo
 * @date : 2023/12/08
 * 切片机器人，让机器人可以被切分成多个机器人，每个机器人监听不同的频道或者群组
 */
data class BotSection(
    /**
     * 当前切片的索引
     * 注意，索引从0开始，如果索引超过(总切片数量和 shares 数量)最大值会报错
     */
    val index: Int = 0,
    /**
     * 总切片数量
     * 注意总切片数量不应该大于 shares 数量，如果超过，请自力更生【是否超过请见日志是否有警告，total 数量不应该超过你机器人加入的频道与群的总量】
     */
    val total: Int = 1,
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BotSection) return false

        if (index != other.index) return false
        if (total != other.total) return false

        return true
    }

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + total
        return result
    }
}
