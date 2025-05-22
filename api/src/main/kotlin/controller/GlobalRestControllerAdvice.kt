package org.team_alilm.controller

import jakarta.validation.ValidationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.team_alilm.application.port.out.gateway.SendSlackGateway
import org.team_alilm.global.error.BasketAlreadyExistsException
import org.team_alilm.global.error.BusinessException
import org.team_alilm.response.ApiResponse

@RestControllerAdvice
class GlobalRestControllerAdvice(
    val slackGateway: SendSlackGateway
) {

    private val log = LoggerFactory.getLogger(GlobalRestControllerAdvice::class.java)

    @ExceptionHandler(value = [BusinessException::class])
    fun handleBusinessException(e: BusinessException): ResponseEntity<ApiResponse<Unit>> {
        log.warn("BusinessException error log : ${e.message}")

        return ResponseEntity.badRequest()
            .body(
                ApiResponse
                    .fail(
                        code = e.error.code,
                        message = e.error.message,
                    )
            )
    }

    @ExceptionHandler(value = [BasketAlreadyExistsException::class])
    fun handleBasketAlreadyExistsException(e: BasketAlreadyExistsException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
    }

    @ExceptionHandler(value = [ValidationException::class])
    fun handleValidationException(e: ValidationException): ResponseEntity<String> {
        log.info("ValidationException error log : ${e.message}")

        return ResponseEntity.badRequest().body(e.message)
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