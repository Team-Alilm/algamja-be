package org.teamalilm.alilmbe.controller.advice

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.teamalilm.alilmbe.controller.body.ExceptionResponseBody
import org.teamalilm.alilmbe.controller.error.RequestValidateException

@RestControllerAdvice
class GlobalRestControllerAdvice {

    private val log = LoggerFactory.getLogger(GlobalRestControllerAdvice::class.java)

    @ExceptionHandler(value = [RequestValidateException::class])
    fun handleRequestValidateException(e: RequestValidateException): ResponseEntity<ExceptionResponseBody> {
        log.error("error log : ${e.message}")

        return ResponseEntity.badRequest().body(
            ExceptionResponseBody(
                message = e.bindingResult.allErrors.joinToString { it.defaultMessage ?: "" }
            )
        )
    }

    @ExceptionHandler(value = [RuntimeException::class])
    fun handleRuntimeException(e: RuntimeException): ResponseEntity<ExceptionResponseBody> {
        log.error("error log : ${e.message}")

        return ResponseEntity.ok().body(
            ExceptionResponseBody(
                message = e.message
            )
        )
    }

    @ExceptionHandler(value = [Exception::class])
    fun handleException(e: Exception): ResponseEntity<ExceptionResponseBody> {
        log.error("error log : ${e.message}")

        return ResponseEntity.internalServerError().body(
            ExceptionResponseBody(
                message = e.message,
            )
        )
    }

}