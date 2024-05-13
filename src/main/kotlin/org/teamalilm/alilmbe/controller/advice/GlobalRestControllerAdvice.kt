package org.teamalilm.alilmbe.controller.advice

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.teamalilm.alilmbe.controller.body.BaseResponseBody

@RestControllerAdvice
class GlobalRestControllerAdvice {

    private val log = LoggerFactory.getLogger(GlobalRestControllerAdvice::class.java)

    @ExceptionHandler(value = [Exception::class])
    fun handleException(e: Exception): ResponseEntity<BaseResponseBody> {
        log.error("error log : ${e.message}")

        return ResponseEntity.internalServerError().body(
            BaseResponseBody(
                code = "error",
                data = e.message ?: "An error occurred"
            )
        )
    }

    @ExceptionHandler(value = [RuntimeException::class])
    fun handleRuntimeException(e: RuntimeException): ResponseEntity<BaseResponseBody> {
        return ResponseEntity.ok().body(
            BaseResponseBody(
                code = "RuntimeException",
                data = e.message ?: ""
            )
        )
    }
}