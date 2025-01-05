package io.github.zimoyin.ra3.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

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
)
