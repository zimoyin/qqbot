package com.github.zimoyin.qqbot.exception

/**
 *
 * @author : zimo
 * @date : 2023/12/07
 */
class HttpClientException(override var message: String = "An exception occurred when the HTTP client accessed and processed the server",throwable: Throwable? = null) : RuntimeException(message,throwable){
    constructor(message: String) : this(message,null) {
        this.message = message
    }

    constructor(throwable: Throwable?) : this("An exception occurred when the HTTP client accessed and processed the server",throwable) {
        this.message = "An exception occurred when the HTTP client accessed and processed the server"
    }
}
class HttpHandlerException(throwable: Throwable? = null) : RuntimeException("Unable to parse or the information returned by the server does not meet the requirements",throwable)
class EventBusException(throwable: Throwable? = null) : RuntimeException("An uncaught exception occurred in the event loop",throwable)
class WebSocketReconnectException(throwable: Throwable? = null) : RuntimeException("The server requires the client to reconnect",throwable)
