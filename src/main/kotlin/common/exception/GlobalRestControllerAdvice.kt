package org.team_alilm.common.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalRestControllerAdvice {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException): ResponseEntity<ErrorResponse> {
        val code = ex.errorCode
        return ResponseEntity
            .status(code.status)
            .body(ErrorResponse.of(code))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val code = ErrorCode.INVALID_INPUT_VALUE

        // 필드 에러: 같은 필드의 중복 메시지 제거
        val fieldMessage = ex.bindingResult.fieldErrors
            .groupBy({ it.field }) { it.defaultMessage ?: "Invalid value" }
            .map { (field, messages) -> "$field: ${messages.distinct().joinToString(", ")}" }

        // 글로벌 에러(object-level)
        val globalMessage = ex.bindingResult.globalErrors
            .map { it.defaultMessage ?: "Invalid request" }

        val message = (fieldMessage + globalMessage)
            .takeIf { it.isNotEmpty() }
            ?.joinToString("; ")
            ?: "요청 값이 올바르지 않습니다."

        return ResponseEntity
            .status(code.status)
            .body(ErrorResponse.of(code, message))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        val code = ErrorCode.INVALID_REQUEST
        return ResponseEntity
            .status(code.status)
            .body(ErrorResponse.of(code, ex.message))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ErrorResponse> {
        val code = ErrorCode.INTERNAL_ERROR
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.of(code, ex.message))
    }

    data class ErrorResponse(
        val errorCode: String,
        val message: String
    ) {
        companion object {
            fun of(errorCode: ErrorCode, overrideMessage: String? = null) =
                ErrorResponse(
                    errorCode = errorCode.code,
                    message = overrideMessage ?: errorCode.message
                )
        }
    }
}