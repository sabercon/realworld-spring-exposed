package cn.sabercon.realworld.web

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.springframework.core.Ordered
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.stereotype.Component
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver

@Component
class GlobalHandlerExceptionResolver(private val mapper: ObjectMapper) : AbstractHandlerExceptionResolver() {

    init {
        order = Ordered.HIGHEST_PRECEDENCE
    }

    override fun doResolveException(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any?,
        ex: Exception,
    ): ModelAndView? {
        val (status, error) = statusAndError(ex)
        if (status == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            logUnknownException(request, ex)
        }

        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.status = status
        response.writer.write(mapper.writeValueAsString(error))
        return ModelAndView()
    }

    private fun statusAndError(ex: Exception): Pair<Int, ErrorResponse> {
        return when (ex) {
            is HttpMediaTypeNotSupportedException -> HttpStatus.UNSUPPORTED_MEDIA_TYPE.value() to ErrorResponse.of(ex)
            is HttpMediaTypeNotAcceptableException -> HttpStatus.NOT_ACCEPTABLE.value() to ErrorResponse.of(ex)
            is HttpMessageNotReadableException -> HttpStatus.UNSUPPORTED_MEDIA_TYPE.value() to ErrorResponse.of(ex)
            is NoSuchElementException -> HttpStatus.NOT_FOUND.value() to ErrorResponse.of(ex)
            is EntityNotFoundException -> HttpStatus.NOT_FOUND.value() to ErrorResponse.of(ex)
            is ResponseStatusException -> ex.statusCode.value() to ErrorResponse.of(ex.reason)
            else -> HttpStatus.INTERNAL_SERVER_ERROR.value() to ErrorResponse.of("UNKNOWN_ERROR")
        }
    }

    private fun logUnknownException(request: HttpServletRequest, ex: Exception) {
        logger.error("Unknown error on ${request.method} ${request.requestURI}", ex)
    }

    private data class ErrorResponse(val body: Body) {
        data class Body(val errors: List<String>)

        companion object {
            fun of(messages: List<String>) = ErrorResponse(Body(messages))

            fun of(message: String?) = if (message.isNullOrEmpty()) of(listOf()) else of(listOf(message))

            fun of(ex: Exception) = of(ex.message)
        }
    }
}
