package io.github.zimoyin.ra3.commander

import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import io.github.zimoyin.ra3.annotations.ICommand
import io.github.zimoyin.ra3.config.ResourcesReleaseConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 *
 * @author : zimo
 * @date : 2025/01/05
 */
abstract class AbsCommander<T : MessageEvent> : ICommand<T> {
    @Autowired
    lateinit var config: ResourcesReleaseConfig
    val logger: Logger = LoggerFactory.getLogger(javaClass)
}