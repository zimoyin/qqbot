package io.github.zimoyin.qqbot.event.supporter

import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.net.bean.Payload

/**
 *
 * @author : zimo
 * @date : 2023/12/08
 */
abstract class AbsEventHandler<T : Event> {
    abstract fun handle(payload: Payload): T
}
