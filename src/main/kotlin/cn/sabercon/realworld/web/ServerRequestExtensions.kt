package cn.sabercon.realworld.web

import cn.sabercon.realworld.util.Jwt
import cn.sabercon.realworld.util.unauthorized
import com.auth0.jwt.exceptions.JWTVerificationException
import org.springframework.beans.factory.getBean
import org.springframework.http.HttpHeaders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.paramOrNull
import org.springframework.web.servlet.support.RequestContextUtils

fun ServerRequest.context(): WebApplicationContext {
    return RequestContextUtils.findWebApplicationContext(this.servletRequest())!!
}

/**
 * Extracts the token from the Authorization header.
 */
fun ServerRequest.token(): String? {
    return headers()
        .header(HttpHeaders.AUTHORIZATION)
        .getOrNull(0)
        ?.takeIf { it.startsWith("Token ", true) }
        ?.substring("Token ".length)
}

/**
 * Verifies the Authorization header and returns the user id.
 *
 * @throws ResponseStatusException if the token not found or invalid.
 */
fun ServerRequest.userId(): String {
    val token = token() ?: unauthorized("Token not found")
    val jwt = context().getBean<Jwt>()

    try {
        return jwt.decodeToken(token)
    } catch (e: JWTVerificationException) {
        unauthorized(e.message)
    }
}

fun ServerRequest.userIdOrNull(): String? {
    return runCatching { userId() }.getOrNull()
}

fun ServerRequest.pageRequest(): PageRequest {
    val offset = paramOrNull("offset")?.toLongOrNull() ?: 0
    val limit = paramOrNull("limit")?.toIntOrNull() ?: 20
    return PageRequest(offset, limit)
}
