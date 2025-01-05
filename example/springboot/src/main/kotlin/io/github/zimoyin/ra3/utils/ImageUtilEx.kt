package io.github.zimoyin.ra3.utils

import io.github.zimoyin.ra3.expand.toBufferedImage
import org.springframework.util.ResourceUtils
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

/**
 *
 * @author : zimo
 * @date : 2025/01/05
 */
class ImageUtilEx {
    companion object {
        fun combineImagesVertically(vararg files: File): BufferedImage {
            // 加载图像文件为 BufferedImage 对象。
            val imageList = files.map { it.toBufferedImage() }.toMutableList()
            return combineImagesVertically(*imageList.toTypedArray())
        }

        fun combineImagesVertically(vararg imageList: BufferedImage): BufferedImage {
            // 获取每个图像的宽度，并假设所有图像的宽度相同或根据需要调整。
            val maxWidth = imageList.maxOf { it.width }
            // 计算总高度。
            val maxHeight = imageList.maxOf { it.height }
            val totalHeight = imageList.sumOf {
                it.height * maxWidth / it.width
            }

            // 创建一个新的 BufferedImage，其大小足以容纳三个图像。
            val combinedImage = BufferedImage(maxWidth, totalHeight, BufferedImage.TYPE_INT_ARGB)
            // 使用 Graphics2D 绘制位图。
            val g2d: Graphics2D = combinedImage.createGraphics()

            var currentY = 0
            for ((index, image) in imageList.withIndex()) {
                val findImage = image.resize(maxWidth, image.height * maxWidth / image.width)
                g2d.drawImage(findImage, 0, currentY, null)
                currentY += findImage.height
            }
            // 释放图形上下文。
            g2d.dispose()

            return combinedImage
        }


        fun addTextToAutoSizeImage(file: File, text: String, width: Int): BufferedImage {
            // 读取输入的图片
            val originalImage = ImageIO.read(file)

            val fontSize = 14f
            // 加载自定义字体
            val font: Font = try {
                val fontStream = ResourceUtils.getFile("classpath:ttf/fzhei.ttf").inputStream()
                Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(fontSize)  // 设置字体大小为30
            } catch (e: IOException) {
                println("字体加载失败: ${e.message}")
                Font("Arial", Font.PLAIN, fontSize.toInt()) // 默认字体
            }


            // 创建一个图形上下文来绘制文字
            val imageGraphics = originalImage.graphics as Graphics2D
            imageGraphics.font = font

            // 启用抗锯齿
            imageGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
            imageGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)


            val x = 15f
            val y = 40f
            // 创建一个新的BufferedImage，宽度由输入参数指定
            val maxFontWidth = width - x * 2 // 留下左右各20像素的空白


            // 根据行高来计算图片的高度
            val textHeight = imageGraphics.calculateAutoWrapHeight(
                text,
                x,
                y,
                maxFontWidth,
                font = font
            ).toInt() + (y * 2).toInt() // 添加上下空白

            // 创建一个新的BufferedImage，宽度由输入参数指定，高度根据文本行数和间距计算
            val finalImage = BufferedImage(width, textHeight, BufferedImage.TYPE_INT_ARGB)
            val finalGraphics = finalImage.createGraphics()

            // 绘制背景图片
            finalGraphics.drawImage(originalImage, 0, 0, width, textHeight, null)

            // 设置文字颜色为白色
            finalGraphics.color = Color.WHITE
            finalGraphics.font = font

            // 继承抗锯齿设置
            finalGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
            finalGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)

            // 使用封装的绘制字符串的函数
            finalGraphics.drawString(text, x, y, maxFontWidth, font = font)

            // 释放图形上下文
            finalGraphics.dispose()

            return finalImage
        }
    }
}