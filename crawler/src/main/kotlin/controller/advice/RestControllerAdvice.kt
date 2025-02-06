package org.team_alilm.adapter.`in`.web.advice

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.team_alilm.error.CustomException

@RestControllerAdvice
class RestControllerAdvice {

    private val log = LoggerFactory.getLogger(org.team_alilm.adapter.`in`.web.advice.RestControllerAdvice::class.java)

    @ExceptionHandler(value = [CustomException::class])
    fun handleMusinsaCrawlingException(e: CustomException): ResponseEntity<Map<String, Any?>> {
        log.error("MusinsaCrawlingException error log: ${e.message}")

        val errorResponse = mapOf(
            "errorCode" to e.errorCode,
            "errorMessage" to e.message
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(value = [RuntimeException::class])
    fun handleRuntimeException(e: RuntimeException): ResponseEntity<String> {
        log.error("RuntimeException error log : ${e.message}")

        return ResponseEntity.badRequest().body(e.message)
    }

    @ExceptionHandler(value = [Exception::class])
    fun handleException(e: Exception): ResponseEntity<String> {
        log.error("Exception error log : ${e.message}")

        return ResponseEntity.internalServerError().body(e.message)
    }

}