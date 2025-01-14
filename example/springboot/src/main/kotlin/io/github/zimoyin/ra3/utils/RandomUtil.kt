package io.github.zimoyin.ra3.utils

import kotlin.math.abs
import kotlin.random.Random

/**
 *
 * @author : zimo
 * @date : 2025/01/06
 */
class RandomUtil private constructor() {
    companion object {

        /**
         * 随机一个布尔值，默认概率为 0.5
         */
        fun randomBoolean(probability: Double = 0.5): Boolean {
            return if (probability > 1) randomElementWithProbability(
                listOf(true, false),
                listOf(probability, 1 - probability)
            )
            else randomElementWithProbability(
                listOf(true, false),
                listOf(probability / (probability * 10), 1 - probability / (probability * 10))
            )
        }

        /**
         * 随机选择一个元素，并根据元素体积进行选择。
         * 每个元素的概率计算公式为：probabilities[i]  / probabilities.sum()
         * @param prizes 待抽取列表
         * @param probabilities 每个元素对应 prizes 中每个元素的体积，总体积就是  probabilities.sum()。如果 sum 大于 1 则概率就请使用概率公式计算
         */
        fun <T> randomElementWithProbability(prizes: List<T>, probabilities: List<Double> = emptyList()): T {
            if (prizes.isEmpty()) throw IllegalArgumentException("prizes is empty")

            if (probabilities.isEmpty()) {
                // 如果没有给定概率，或者概率数组的长度与元素数组的长度不匹配，则假设平等概率
                return prizes[Random.nextInt(prizes.size)]
            }

            val adjustedProbabilities = probabilities.toDoubleArray()

            // 如果概率数组的长度小于元素数组的长度，则将剩余的概率均匀分配给剩余的元素
            if (adjustedProbabilities.size < prizes.size) {
                val remainingProbability =
                    abs((1.0 - adjustedProbabilities.sum())) / (prizes.size - adjustedProbabilities.size)
                for (i in adjustedProbabilities.indices) {
                    if (adjustedProbabilities[i] == 0.0) {
                        adjustedProbabilities[i] = remainingProbability
                    }
                }
            }

            // 首先计算概率的总和
            val totalProbability = adjustedProbabilities.sum()

            // 生成一个随机数，该随机数落在 [0, totalProbability) 范围内
            val randomValue = Random.nextDouble(0.0, totalProbability)

            // 将概率分布看作是一个累积的概率密度函数（CDF），每个元素的累积概率表示了从头到当前元素的概率分布范围。
            // 通过比较随机值与每个元素的累积概率，可以确定落在哪个区间内，从而选择对应的元素。
            var cumulativeProbability = 0.0
            for (i in prizes.indices) {
                // 累加每个元素的概率: 不对元素进行排序，而是对元素进行累加，然后进行比较
                cumulativeProbability += adjustedProbabilities[i]
                // 如果随机值落在当前累加的概率区间内，则返回对应的元素
                if (randomValue <= cumulativeProbability) {
                    return prizes[i]
                }
            }

            // 如果由于浮点数精度问题导致未能选择元素，则随机返回一个
            return prizes[Random.nextInt(prizes.size)]
        }
    }
}