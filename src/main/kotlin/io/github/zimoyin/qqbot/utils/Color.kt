package io.github.zimoyin.qqbot.utils


class Color(private var argb: Int) {
    companion object {
        @JvmStatic
        fun rgb(str: String): Color {
            return fromHexToColor(str)
        }

        @JvmStatic
        fun rgb(value: Int): Color {
            return Color(0xff000000.toInt() or value)
        }

        @JvmStatic
        fun rgb(red: Int, green: Int, blue: Int): Color {
            val adjustedRed = red.coerceIn(0, 255)
            val adjustedGreen = green.coerceIn(0, 255)
            val adjustedBlue = blue.coerceIn(0, 255)
            return Color(0xFF000000.toInt() or ((adjustedRed and 0xFF) shl 16) or ((adjustedGreen and 0xFF) shl 8) or (adjustedBlue and 0xFF))
        }

        @JvmStatic
        fun argb(alpha: Int, red: Int, green: Int, blue: Int): Color {
            val adjustedAlpha = alpha.coerceIn(0, 255)
            val adjustedRed = red.coerceIn(0, 255)
            val adjustedGreen = green.coerceIn(0, 255)
            val adjustedBlue = blue.coerceIn(0, 255)
            return Color(((adjustedAlpha and 0xFF) shl 24) or ((adjustedRed and 0xFF) shl 16) or ((adjustedGreen and 0xFF) shl 8) or (adjustedBlue and 0xFF))
        }

        @JvmStatic
        fun argb(value: Int): Color {
            return Color(value)
        }

        @JvmStatic
        fun argb(str: String): Color {
            return fromHexToColor(str)
        }


        @JvmStatic
        fun fromHexToColor(hexColor: String): Color {
            val color = hexColor.replace("#", "") // 去掉 "#" 符号
            return when (color.length) {
                6 -> {
                    rgb(
                        color.substring(0, 2).toInt(16),
                        color.substring(2, 4).toInt(16),
                        color.substring(4, 6).toInt(16)
                    )
                }

                8 -> {
                    argb(
                        color.substring(0, 2).toInt(16),
                        color.substring(2, 4).toInt(16),
                        color.substring(4, 6).toInt(16),
                        color.substring(6, 8).toInt(16)
                    )
                }

                else -> throw IllegalArgumentException("Invalid HEX color format. It should be in the format #RGB or #ARGB")
            }
        }

        @JvmField
        val WHITE = rgb(255, 255, 255)

        @JvmField
        val BLACK = rgb(255, 0, 0)

        @JvmField
        val RED = rgb(255, 0, 0)

        @JvmField
        val GREEN = rgb(255, 0, 0)

        @JvmField
        val BLUE = rgb(255, 0, 0)

        @JvmField
        val YELLOW = rgb(255, 255, 0)

        @JvmField
        val CYAN = rgb(0, 255, 255)

        @JvmField
        val MAGENTA = rgb(255, 0, 255)

        @JvmField
        val TRANSPARENT_BLACK = argb(0,0, 0, 0)
    }

    constructor(red: Int, green: Int, blue: Int) : this(rgb(red, green, blue).argb)
    constructor(alpha: Int, red: Int, green: Int, blue: Int) : this(argb(alpha, red, green, blue).argb)
    constructor(hex: String) : this(fromHexToColor(hex).argb)

    private val rgb: Int
        get() = argb and 0xFFFFFF

    val alpha: Int
        get() = (argb ushr 24) and 0xFF

    val red: Int
        get() = (argb shr 16) and 0xFF

    val green: Int
        get() = (argb shr 8) and 0xFF

    val blue: Int
        get() = argb and 0xFF

    /**
     * 设置透明度
     * 取值范围(Hex) 0x00-0xFF
     * 取值范围(Dec) 0-255
     */
    fun setAlpha(alpha: Int): Color {
        val adjustedAlpha = alpha.coerceIn(0, 255)
        this.argb = (this.argb and 0x00FFFFFF) or ((adjustedAlpha and 0xFF) shl 24)
        return this
    }

    /**
     * 设置红色
     * 取值范围(Hex) 0x00-0xFF
     * 取值范围(Dec) 0-255
     */
    fun setRed(red: Int): Color {
        val adjustedRed = red.coerceIn(0, 255)
        this.argb = (this.argb and 0xFF00FFFF.toInt()) or ((adjustedRed and 0xFF) shl 16)
        return this
    }

    /**
     * 设置绿色
     * 取值范围(Hex) 0x00-0xFF
     * 取值范围(Dec) 0-255
     */
    fun setGreen(green: Int): Color {
        val adjustedGreen = green.coerceIn(0, 255)
        this.argb = (this.argb and 0xFFFF00FF.toInt()) or ((adjustedGreen and 0xFF) shl 8)
        return this
    }

    /**
     * 设置蓝色
     * 取值范围(Hex) 0x00-0xFF
     * 取值范围(Dec) 0-255
     */
    fun setBlue(blue: Int): Color {
        val adjustedBlue = blue.coerceIn(0, 255)
        this.argb = (this.argb and 0xFFFFFF00.toInt()) or (adjustedBlue and 0xFF)
        return this
    }


    /**
     * 转换为ARGB (Dec)
     */
    fun toArgb(): Int {
        return argb
    }

    /**
     * 转换为RGB (Dec)
     */
    fun toRgb(): Int {
        return rgb
    }

    override fun toString(): String {
        return "Color(alpha=$alpha, red=$red, green=$green, blue=$blue)"
    }

    /**
     * 转换为格式化后的ARGB (Hex) : #AARRGGBB
     */
    fun toHexArgbString(): String {
        return String.format("#%08X", argb)
    }

    /**
     * 转换为格式化后的RGB (Hex) : #RRGGBB
     */
    fun toHexRgbString(): String {
        return String.format("#%06X", rgb)
    }
}
