package org.teamalilm.alilm.adapter.`in`.web.advice

import com.google.api.pathtemplate.ValidationException
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RestControllerAdvice {

    private val log = LoggerFactory.getLogger(RestControllerAdvice::class.java)

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