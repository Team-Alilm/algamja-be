package org.teamalilm.alilm.adapter.`in`.web.advice

import com.google.api.pathtemplate.ValidationException
import io.netty.handler.codec.http.HttpResponseStatus.CONFLICT
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.teamalilm.alilm.common.error.BasketAlreadyExistsException

@RestControllerAdvice
class RestControllerAdvice {

    private val log = LoggerFactory.getLogger(RestControllerAdvice::class.java)

    @ExceptionHandler(value = [BasketAlreadyExistsException::class])
    fun handleBasketAlreadyExistsException(e: BasketAlreadyExistsException): ResponseEntity<String> {
        return ResponseEntity.status(CONFLICT.code()).body(e.message)
    }

    @ExceptionHandler(value = [ValidationException::class])
    fun handleValidationException(e: ValidationException): ResponseEntity<String> {
        log.info("error log : ${e.message}")

        return ResponseEntity.badRequest().body(e.message)
    }

    @ExceptionHandler(value = [RuntimeException::class])
    fun handleRuntimeException(e: RuntimeException): ResponseEntity<String> {
        log.error("error log : ${e.message}")

        return ResponseEntity.badRequest().body(e.message)
    }

    @ExceptionHandler(value = [Exception::class])
    fun handleException(e: Exception): ResponseEntity<String> {
        log.error("error log : ${e.message}")

        return ResponseEntity.internalServerError().body(e.message)
    }

}