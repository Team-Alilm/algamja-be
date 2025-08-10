package org.team_alilm.common.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class GlobalRestControllerAdvice {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException): ResponseEntity<ErrorResponse> {
        val code = ex.errorCode
        return ResponseEntity
            .status(code.status)
            .body(ErrorResponse.of(code, ex.message))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val code = ErrorCode.INVALID_INPUT_VALUE
        val message = ex.bindingResult.fieldErrors.joinToString(", ") {
            "${it.field}: ${it.defaultMessage}"
        }
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

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> {
        val code = ErrorCode.INVALID_REQUEST
        val message = "요청 파라미터 타입이 올바르지 않습니다. ${ex.name}=${ex.value}"
        return ResponseEntity
            .status(code.status)
            .body(ErrorResponse.of(code, message))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ErrorResponse> {
        val code = ErrorCode.INTERNAL_ERROR
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.of(code, ex.message))
    }

    data class ErrorResponse(
        val status: Int,
        val errorCode: String,
        val message: String
    ) {
        companion object {
            fun of(errorCode: ErrorCode, overrideMessage: String? = null) =
                ErrorResponse(
                    status = errorCode.status.value(),
                    errorCode = errorCode.code,
                    message = overrideMessage ?: errorCode.message
                )
        }
    }
}