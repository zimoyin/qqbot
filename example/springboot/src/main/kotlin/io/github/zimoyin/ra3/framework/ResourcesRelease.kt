package io.github.zimoyin.ra3.framework

import io.github.zimoyin.ra3.config.ResourcesReleaseConfig
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import org.springframework.core.io.Resource

/**
 *
 * @author : zimo
 * @date : 2025/01/04
 */
@Component
class ResourcesRelease(val config:ResourcesReleaseConfig) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    private fun extractImages() {
        // 创建目标目录
        val destDir = File(config.targetPath)
        if (!destDir.exists()) {
            Files.createDirectories(destDir.toPath())
        }

        val resolver = PathMatchingResourcePatternResolver()
        val resources: Array<Resource> = resolver.getResources("${config.sourcePath}**/*.*")

        resources.forEach { resource ->
            val fileName = resource.filename ?: return@forEach
            val destFilePath: Path = Paths.get(config.targetPath, fileName)

            // 确保目标文件夹存在
            Files.createDirectories(destFilePath.parent)

            try {
                resource.inputStream.use { input ->
                    Files.copy(input, destFilePath, StandardCopyOption.REPLACE_EXISTING)
                }
            } catch (e: Exception) {
                logger.error("Failed to extract resource: ${resource.filename}", e)
            }
        }
    }
}