package org.team_alilm.response

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

object ApiResponseFactory {

    fun <T> ok(data: T?, message: String = "요청이 성공했습니다."): ResponseEntity<ApiResponse<T>> {
        return ResponseEntity(ApiResponse.success(data, message), HttpStatus.OK)
    }

    fun badRequest(message: String): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity(ApiResponse.fail(message = message), HttpStatus.BAD_REQUEST)
    }

    fun internalServerError(message: String = "서버 오류가 발생했습니다."): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity(ApiResponse.fail(message = message), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}