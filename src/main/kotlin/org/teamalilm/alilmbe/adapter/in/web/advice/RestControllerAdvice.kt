package org.teamalilm.alilmbe.adapter.`in`.web.advice

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.teamalilm.alilmbe.web.adapter.error.RequestValidateException

@RestControllerAdvice
class RestControllerAdvice {

    private val log = LoggerFactory.getLogger(RestControllerAdvice::class.java)

    @ExceptionHandler(value = [RequestValidateException::class])
    fun handleRequestValidateException(e: RequestValidateException): ResponseEntity<String> {
        log.error("error log : ${e.message}")

        return ResponseEntity
            .badRequest()
            .body(e.bindingResult.allErrors.joinToString {
                it.defaultMessage ?: ""
            })
    }

    @ExceptionHandler(value = [RuntimeException::class])
    fun handleRuntimeException(e: RuntimeException): ResponseEntity<String> {
        log.error("error log : ${e.message}")

        return ResponseEntity.ok().body(e.message)
    }

    @ExceptionHandler(value = [Exception::class])
    fun handleException(e: Exception): ResponseEntity<String> {
        log.error("error log : ${e.message}")

        return ResponseEntity.internalServerError().body(e.message)
    }

}