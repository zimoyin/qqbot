package io.github.zimoyin.ra3.utils

import org.springframework.util.ResourceUtils
import java.awt.*
import java.awt.image.BufferedImage
import java.io.*
import java.util.*
import java.util.regex.Pattern
import javax.imageio.ImageIO
import javax.swing.ImageIcon


/**
 *
 * @author : zimo
 * @date : 2024/04/05
 */
object ImageUtil {
    fun registerFont(input: InputStream): Font {
        var font: Font? = null

        font = Font.createFont(Font.TRUETYPE_FONT, input)
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font)

        return font!!
    }

    /**
     * 图片合成
     * @param imageA 第一张图片
     * @param imageB 第二张图片
     * @param imageHeight 画布的高度
     * @param imageWidth 画布的宽度
     * @param x1 第一张图片的 x 坐标，默认为 0
     * @param y1 第一张图片的 y 坐标，默认为 0
     * @param x2 第二张图片的 x 坐标，默认为 0
     * @param y2 第二张图片的 y 坐标，默认为 0
     * @param alpha 透明度，用于设置第一张图片
     */
    fun composite(
        imageA: BufferedImage,
        imageB: BufferedImage,
        imageHeight: Int = imageA.height,
        imageWidth: Int = imageB.width,
        x1: Int = 0,
        y1: Int = 0,
        x2: Int = 0,
        y2: Int = 0,
        alpha: Float = 1f,
    ): BufferedImage {
        val combinedImage = BufferedImage(imageHeight, imageWidth, BufferedImage.TYPE_INT_ARGB)
        val g2d = combinedImage.createGraphics()
        g2d.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)
        g2d.drawImage(imageA, x1, y1, null)
        g2d.drawImage(imageB, x2, y2, null)
        g2d.dispose()
        return combinedImage
    }

    /**
     * 图片缩放
     */
    fun resizeImage(originalImage: BufferedImage, targetWidth: Int, targetHeight: Int): BufferedImage {
        val resizedImage = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB)
        val g2d = resizedImage.createGraphics()
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null)
        g2d.dispose()
        return resizedImage
    }

    /**
     * 图片裁剪
     * @param originalImage 原始图像
     * @param startX 裁剪区域的起始 x 坐标
     * @param startY 裁剪区域的起始 y 坐标
     * @param endX 裁剪区域的结束 x 坐标
     * @param endY 裁剪区域的结束 y 坐标
     *
     */
    fun cropImage(originalImage: BufferedImage, startX: Int, startY: Int, endX: Int, endY: Int): BufferedImage {
        // 边界检查并修正
        val correctedStartX = startX.coerceIn(0, originalImage.width - 1)
        val correctedStartY = startY.coerceIn(0, originalImage.height - 1)
        val correctedEndX = endX.coerceIn(0, originalImage.width)
        val correctedEndY = endY.coerceIn(0, originalImage.height)

        // 计算裁剪区域的宽度和高度
        val width = correctedEndX - correctedStartX
        val height = correctedEndY - correctedStartY

        // 创建裁剪区域对象
        val cropRect = Rectangle(correctedStartX, correctedStartY, width, height)

        // 使用 BufferedImage 的 getSubimage 方法裁剪图像
        val croppedImage = originalImage.getSubimage(cropRect.x, cropRect.y, cropRect.width, cropRect.height)

        return croppedImage
    }

    /**
     * 修改图片的透明度
     */
    fun setImageAlpha(image: BufferedImage, alpha: Float = 0.5f): BufferedImage {
        val transparentImage = BufferedImage(
            image.width, image.height, BufferedImage.TYPE_INT_ARGB
        )

        val g2d = transparentImage.createGraphics()
        g2d.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)

        // 绘制原始图像到带有透明度的图像上
        g2d.drawImage(image, 0, 0, null)
        g2d.dispose()

        return transparentImage
    }

    fun toBase64(image: BufferedImage, format: String = "png"): String {
        val outputStream = ByteArrayOutputStream()
        ImageIO.write(image, format, outputStream)
        val imageBytes = outputStream.toByteArray()
        return Base64.getEncoder().encodeToString(imageBytes)
    }

    fun toBytes(image: BufferedImage, format: String = "png"): ByteArray {
        val outputStream = ByteArrayOutputStream()
        ImageIO.write(image, format, outputStream)
        val imageBytes = outputStream.toByteArray()
        return imageBytes
    }

    fun loadImage(filePath: String): BufferedImage {
        return ImageIO.read(File(filePath))
    }

    fun loadResourceImage(path: String): BufferedImage {
        return loadImage(ResourceUtils.getFile(path).inputStream())
    }

    fun loadImage(filePath: File): BufferedImage {
        return ImageIO.read(filePath)
    }

    fun loadImage(input: InputStream): BufferedImage {
        return ImageIO.read(input)
    }

    fun loadImage(bytes: ByteArray): BufferedImage {
        val bis = ByteArrayInputStream(bytes)
        return ImageIO.read(bis)
    }

    fun saveImage(image: BufferedImage, filePath: String = "./image.png", formatName: String = "PNG") {
        ImageIO.write(image, formatName, File(filePath))
    }

    fun createImage(width: Int, height: Int, imageType: Int): BufferedImage {
        return BufferedImage(width, height, imageType)
    }

    /**
     * 图片亮度调整
     * @param image 图片
     * @param param 在当前亮度基础上添加或者减去亮度
     * @throws IOException
     */
    fun setImageLight(image: BufferedImage, param: Int): BufferedImage {
        for (i in 0 until image.width) {
            for (j in 0 until image.height) {
                var rgb = image.getRGB(i, j)
                val r = (rgb shr 16 and 0xff) + param
                val g = (rgb shr 8 and 0xff) + param
                val b = (rgb and 0xff) + param

                rgb = (clamp(255) and 0xff shl 24) or
                        (clamp(r) and 0xff shl 16) or
                        (clamp(g) and 0xff shl 8) or
                        (clamp(b) and 0xff)

                image.setRGB(i, j, rgb)
            }
        }
        return image
    }

    /**
     * 给图片设置透明度
     */
    @Deprecated("")
    fun setImageAlpha2(image: BufferedImage, alpha: Float): BufferedImage {
        for (i in 0 until image.width) {
            for (j in 0 until image.height) {
                var rgb = image.getRGB(i, j)
                val alphaChannel = rgb shr 24 and 0xff
                val newAlpha = clamp(alphaChannel + (alpha * 255).toInt())
                rgb = (newAlpha shl 24) or (rgb and 0x00ffffff)
                image.setRGB(i, j, rgb)
            }
        }
        return image
    }

    fun getImageLight(image: BufferedImage): Int {
        // 计算亮度
        var totalBrightness = 0.0
        val totalPixels = image.width * image.height
        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val rgb = image.getRGB(x, y)
                val red = (rgb shr 16) and 0xFF
                val green = (rgb shr 8) and 0xFF
                val blue = rgb and 0xFF
                // 使用简单的平均值法计算亮度
                val brightness = (red + green + blue) / 3.0
                totalBrightness += brightness
            }
        }

        // 计算平均亮度
        return (totalBrightness / totalPixels).toInt()
    }

    // 判断a,r,g,b值，大于256返回256，小于0则返回0,0到256之间则直接返回原始值
    private fun clamp(rgb: Int): Int {
        return when {
            rgb > 255 -> 255
            rgb < 0 -> 0
            else -> rgb
        }
    }


    /**
     * 按设置的宽度高度比例压缩图片文件,如果width和height都大于0，则以此为基准进行压缩（可能造成图片变型）
     *
     * @param width 宽度
     * @param height 高度
     * @return 返回压缩后的文件的全路径
     */
    @Deprecated("")
    fun compressImage(image: BufferedImage, width0: Int, height0: Int): BufferedImage {
        var width = width0
        var height = height0
        if (width <= 0 && height <= 0) {
            return image
        }
        /* 对服务器上的临时文件进行处理 */
        /* 按比例压缩 */
        val w = image.getWidth(null)
        val h = image.getHeight(null)
        val bili: Double
        if (width > 0 && height <= 0) {
            bili = width / w.toDouble()
            height = (h * bili).toInt()
        } else if (height > 0 && width <= 0) {
            bili = height / h.toDouble()
            width = (w * bili).toInt()
        }

        val buffImg = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val graphics = buffImg.createGraphics()
        graphics.fontRenderContext
        graphics.background = Color(255, 255, 255)
        graphics.color = Color(255, 255, 255)
        graphics.fillRect(0, 0, width, height)
        graphics.drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null)
        return buffImg
    }

    /**
     * 灰度化图片
     */
    fun grayImage(image: BufferedImage): BufferedImage {
        val width = image.width
        val height = image.height
        val grayImage = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)
        for (i in 0 until width) {
            for (j in 0 until height) {
                val rgb = image.getRGB(i, j)
                grayImage.setRGB(i, j, rgb)
            }
        }
        return grayImage
    }

    /**
     * 二值化图片
     */
    fun binaryImage(image: BufferedImage): BufferedImage {
        val width = image.width
        val height = image.height
        val binaryImage = BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY)
        for (i in 0 until width) {
            for (j in 0 until height) {
                val rgb = image.getRGB(i, j)
                binaryImage.setRGB(i, j, rgb)
            }
        }
        return binaryImage
    }

    /**
     * 透明化图片
     * @param image 图片
     * @param rcb  要扣掉的rcb色值
     */
    fun transAlpha(image: BufferedImage, rcb: Int): BufferedImage {
        var colorRange: Int = 210
        val pattern: Pattern = Pattern.compile("[0-9]*")
        fun isNo(str: String?): Boolean {
            return pattern.matcher(str).matches()
        }

        fun colorInRange(color: Int): Boolean {
            val red = (color and 0xff0000) shr 16
            val green = (color and 0x00ff00) shr 8
            val blue = (color and 0x0000ff)
            return red >= colorRange && green >= colorRange && blue >= colorRange
        }


        val imageIcon = ImageIcon(image)
        val bufferedImage = BufferedImage(
            imageIcon.iconWidth, imageIcon.iconHeight,
            BufferedImage.TYPE_4BYTE_ABGR
        )
        val g2D = bufferedImage.graphics as Graphics2D
        g2D.drawImage(imageIcon.image, 0, 0, imageIcon.imageObserver)
        var alpha = 0
        colorRange = 255 - rcb // 0
        for (j1 in bufferedImage.minY until bufferedImage
            .height) {
            for (j2 in bufferedImage.minX until bufferedImage
                .width) {
                var rgb = bufferedImage.getRGB(j2, j1)
                alpha = if (colorInRange(rgb)) {
                    0
                } else {
                    255
                }
                rgb = (alpha shl 24) or (rgb and 0x00ffffff)
                bufferedImage.setRGB(j2, j1, rgb)
            }
        }

        g2D.drawImage(bufferedImage, 0, 0, imageIcon.imageObserver)
        return bufferedImage
    }
}

/**
 * 绘制图片，并允许指定该图片的透明度
 */
fun Graphics2D.drawImage(image: BufferedImage, x: Int, y: Int, alpha: Float) {
    val composite0 = this.composite
    composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)
    drawImage(image, x, y, null)
    composite = composite0
}

/**
 * 设置画笔下一次绘制的透明度
 */
fun Graphics2D.setAlpha(alpha: Float) {
    composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)
}

/**
 * 绘制文本，并允许指定该文本的字体大小
 */
fun Graphics2D.drawString(style: Float, text: String, x: Float, y: Float) {
    this.font.let {
        this.font = font.deriveFont(style)
        this.drawString(text, x, y)
        this.font = it
    }
}

/**
 * 绘制文本，并允许指定该文本的字体等样式
 */
fun Graphics2D.drawString(font: Font, text: String, x: Float, y: Float) {
    this.font.let {
        this.font = font
        this.drawString(text, x, y)
        this.font = it
    }
}

/**
 * 绘制文本，并允许指定该文本的字体大小
 */
fun Graphics2D.drawString(style: Float, text: String, x: Int, y: Int) {
    drawString(font.deriveFont(style), text, x.toFloat(), y.toFloat())
}

/**
 * 绘制文本，并允许指定该文本的字体等样式
 */
fun Graphics2D.drawString(font: Font, text: String, x: Int, y: Int) {
    drawString(font, text, x.toFloat(), y.toFloat())
}

/**
 * 绘制文本，并允许指定文本长度和行高，并自动换行
 * @param text 要绘制的文本
 * @param x0 绘制文本的起始X坐标
 * @param y0 绘制文本的起始Y坐标
 * @param maxWidth 绘制文本的最大宽度
 * @param lineHeight 绘制文本的行高
 * @param font 要绘制文本的字体
 * @param style 要绘制文本的字体样式
 */
fun Graphics2D.drawString(
    text: String,
    x0: Int,
    y0: Int,
    maxWidth: Int = Int.MAX_VALUE,
    lineHeight: Int = fontMetrics.height,
    font: Font = this.font,
    style: Float = -1f,
): Int {
    return drawString(text, x0.toFloat(), y0.toFloat(), maxWidth.toFloat(), lineHeight.toFloat(), font, style)
}

/**
 * 绘制文本，并允许指定文本长度和行高，并自动换行
 * @param text 要绘制的文本
 * @param x0 绘制文本的起始X坐标
 * @param y0 绘制文本的起始Y坐标
 * @param maxWidth 绘制文本的最大宽度
 * @param lineHeight 绘制文本的行高
 * @param font 要绘制文本的字体
 * @param style 要绘制文本的字体样式
 */
fun Graphics2D.drawString(
    text: String,
    x0: Float,
    y0: Float,
    maxWidth: Float = Float.MAX_VALUE,
    lineHeight: Float = fontMetrics.height.toFloat(),
    font: Font = this.font,
    style: Float = -1f,
): Int {
    this.font = font
    if (style > -1f) {
        this.font = font.deriveFont(style)
    }
    val x = x0
    var y = y0
    val fontMetrics = fontMetrics
    var lineStart = 0
    var lineEnd = 0

    while (lineEnd < text.length) {
        // 如果加上下一个字符会导致超出最大宽度，则结束当前行
        val fontWidth = fontMetrics.charsWidth(text.toCharArray(), lineEnd, 1).let {
            it + fontMetrics.stringWidth(text.substring(lineStart, lineEnd))
        }
        if (fontWidth > maxWidth || text[lineEnd] == '\n') {
            // 绘制当前行
            drawString(text.substring(lineStart, lineEnd), x, y)
            // 移动到下一行
            y += lineHeight
            lineStart = lineEnd
        }
        lineEnd++
    }

    // 绘制最后一行
    if (lineStart < text.length) {
        drawString(text.substring(lineStart), x, y)
    }

    return lineEnd
}

fun Graphics2D.calculateAutoWrapHeight(
    text: String,
    x0: Float,
    y0: Float,
    maxWidth: Float = Float.MAX_VALUE,
    lineHeight: Float = fontMetrics.height.toFloat(),
    font: Font = this.font,
    style: Float = -1f,
): Float {
    this.font = font
    if (style > -1f) {
        this.font = font.deriveFont(style)
    }
    var y = y0
    val fontMetrics = fontMetrics
    var lineStart = 0
    var lineEnd = 0


    while (lineEnd < text.length) {
        // 如果加上下一个字符会导致超出最大宽度，则结束当前行
        val fontWidth = fontMetrics.charsWidth(text.toCharArray(), lineEnd, 1).let {
            it + fontMetrics.stringWidth(text.substring(lineStart, lineEnd))
        }
        if (fontWidth > maxWidth|| text[lineEnd] == '\n') {
            // 移动到下一行
            y += lineHeight
            lineStart = lineEnd
        }
        lineEnd++
    }

    return y - y0
}

fun Graphics2D.drawAlphaImage(image: BufferedImage, x: Int, y: Int, alpha: Float) {
    this.composite.let {
        setAlpha(alpha)
        drawImage(image, x, y, null)
        this.composite = it
    }
}

fun Graphics2D.drawImage(image: BufferedImage, x: Int, y: Int) {
    drawImage(image, x, y, null)
}

fun Graphics2D.drawAlphaImage(image: BufferedImage, x: Int, y: Int, width: Int, height: Int, alpha: Float) {
    this.composite.let {
        setAlpha(alpha)
        drawImage(image, x, y, width, height, null)
        this.composite = it
    }
}

fun Graphics2D.drawImage(image: BufferedImage, x: Int, y: Int, width: Int, height: Int) {
    drawImage(image, x, y, width, height, null)
}

fun BufferedImage.resize(width: Int, height: Int): BufferedImage {
    return ImageUtil.resizeImage(this, width, height)
}

fun BufferedImage.createGraphics(alpha: Float, rule: Int = AlphaComposite.SRC_OVER): Graphics2D {
    return this.createGraphics().apply {
        composite = AlphaComposite.getInstance(rule, alpha)
    }
}