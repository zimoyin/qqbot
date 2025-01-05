package io.github.zimoyin.ra3.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.util.ResourceUtils
import java.io.File

/**
 *
 * @author : zimo
 * @date : 2025/01/04
 */
@Configuration
@ConfigurationProperties(prefix = "qqbot.resources-release")
data class ResourcesReleaseConfig(
    var sourcePath: String = "classpath:images/",
    var targetPath: String = "./data/images/"
) {
    val targetFile by lazy {
        File(targetPath)
    }

    val targetFileList by lazy {
        targetFile.listFiles()
    }

    fun image(name: String): File {
        return if (isExistsFile(name)) targetFile.resolve(name) else throw IllegalArgumentException("$name image not found")
    }

    fun isExistsFile(name: String): Boolean {
        return targetFileList.any { it.name == name }
    }

    fun getFile(name: String): File {
        return ResourceUtils.getFile("$sourcePath$name")
    }

    companion object{
        const val COMMAND_BACKGROUND_IMAGE = "command_background.png"
        const val CAMP_1_IMAGE = "camp_1.png"
        const val CAMP_2_IMAGE = "camp_2.png"
        const val CAMP_3_IMAGE = "camp_3.png"
        const val TOURIST_TRAMCAR_IMAGE = "tourist_tramcar.png"
        const val DIVIDER_IMAGE = "divider.png"
        const val CAMP_2_ENTITY_TRAMCAR_1_IMAGE = "camp_2_entity_tramcar_1.png"
    }
}
