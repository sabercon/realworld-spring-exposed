package cn.sabercon.realworld.util

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

fun unauthorized(message: String? = null): Nothing =
    throw ResponseStatusException(HttpStatus.UNAUTHORIZED, message)

fun forbidden(message: String? = null): Nothing =
    throw ResponseStatusException(HttpStatus.FORBIDDEN, message)

fun notFound(message: String? = null): Nothing =
    throw ResponseStatusException(HttpStatus.NOT_FOUND, message)

fun unprocessable(message: String? = null): Nothing =
    throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, message)
