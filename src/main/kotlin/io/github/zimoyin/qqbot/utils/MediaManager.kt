package io.github.zimoyin.qqbot.utils

import io.github.zimoyin.qqbot.net.bean.message.send.MediaMessageBean
import java.io.Serializable

/**
 * 用于管理已经上传的资源
 * @author : zimo
 * @date : 2024/11/26
 */
class MediaManager : HashMap<String, MediaMessageBean>(), Serializable {
    companion object {
        /**
         * 是否启用
         */
        @JvmStatic
        var isEnable = true

        @JvmStatic
        var instance = MediaManager()
    }

    override fun get(key: String): MediaMessageBean? {
        val media = super.get(key) ?: return null
        if (media.isExpired()){
            remove(key)
            return null
        }
        return media
    }
}
