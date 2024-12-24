package io.github.zimoyin.qqbot.exception

/**
 *
 * @author : zimo
 * @date : 2023/12/07
 */
class HttpClientException(
    override var message: String = "An exception occurred when the HTTP client accessed and processed the server",
    throwable: Throwable? = null,
) : RuntimeException(message, throwable) {
    constructor(message: String) : this(message, null) {
        this.message = message
    }

    constructor(throwable: Throwable?) : this(
        "An exception occurred when the HTTP client accessed and processed the server",
        throwable
    ) {
        this.message = "An exception occurred when the HTTP client accessed and processed the server"
    }
}

class HttpHandlerException(
    var msg: String = "Unable to parse or the information returned by the server does not meet the requirements",
    throwable: Throwable? = null,
) : RuntimeException(
    msg,
    throwable
) {
    constructor(message: String) : this(message, null)
    constructor(throwable: Throwable) : this(
        "Unable to parse or the information returned by the server does not meet the requirements",
        throwable
    )
}

class HttpMessageStateException(
    var msg: String = "The server returns an error message status code",
    throwable: Throwable? = null,
) : RuntimeException(msg, throwable) {
    constructor(message: String) : this(message, null) {
        this.msg = message
    }

    constructor(throwable: Throwable?) : this(
        "The server returns an error message status code",
        throwable
    )
}

class HttpStateCodeException(
    var msg: String = "The server returns an error status code",
    throwable: Throwable? = null,
) : RuntimeException(msg, throwable) {
    constructor(message: String) : this(message, null) {
        this.msg = message
    }

    constructor(throwable: Throwable?) : this(
        "The server returns an error status code",
        throwable
    )
}

class EventBusException(throwable: Throwable? = null) :
    RuntimeException("An uncaught exception occurred in the event loop", throwable)

class WebSocketReconnectException(throwable: Throwable? = null) :
    RuntimeException("The server requires the client to reconnect", throwable)

open class CommandException(
    message: String = "An exception occurred when the command was executed",
    throwable: Throwable? = null,
) : RuntimeException(message, throwable)

class CommandHandlerException(
    msg: String = "An exception occurred when the command handler processed the command",
    throwable: Throwable? = null,
) :
    CommandException(msg, throwable)

class CommandNotFoundException(msg: String = "The command was not found", throwable: Throwable? = null) :
    CommandException(msg, throwable)
