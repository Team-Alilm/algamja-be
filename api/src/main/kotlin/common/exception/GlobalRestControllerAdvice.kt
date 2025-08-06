package org.team_alilm.common.exception

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.team_alilm.global.error.BusinessException
import org.team_alilm.response.ApiResponse
import java.util.NoSuchElementException

@RestControllerAdvice
class GlobalRestControllerAdvice {

    private val log = LoggerFactory.getLogger(GlobalRestControllerAdvice::class.java)

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ApiResponse<Unit>> {
        log.warn("BusinessException: ${e.errorCode.code} - ${e.message}")
        return ResponseEntity
            .status(e.errorCode.status)
            .body(
                ApiResponse.Companion.fail(
                e.errorCode.code,
                e.errorCode.message
            ))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException): ResponseEntity<ApiResponse<Unit>> {
        log.warn("IllegalArgumentException: ${e.message}")
        return ResponseEntity.badRequest()
            .body(ApiResponse.Companion.fail("INVALID_ARGUMENT", e.message ?: "잘못된 요청입니다."))
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElement(e: NoSuchElementException): ResponseEntity<ApiResponse<Unit>> {
        log.warn("NoSuchElementException: ${e.message}")
        return ResponseEntity.status(404)
            .body(ApiResponse.Companion.fail("NOT_FOUND", e.message ?: "대상을 찾을 수 없습니다."))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Unit>> {
        log.error("Unhandled Exception: ", e)
        return ResponseEntity.internalServerError()
            .body(ApiResponse.Companion.fail("INTERNAL_ERROR", "알 수 없는 서버 오류가 발생했습니다."))
    }
}