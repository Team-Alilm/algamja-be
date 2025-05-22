package org.team_alilm.response

data class ApiResponse<T>(
    val code: String,
    val message: String,
    val data: T? = null
) {
    companion object {
        fun <T> success(data: T?, message: String = "요청이 성공했습니다."): ApiResponse<T> {
            return ApiResponse(code = "SUCCESS", message = message, data = data)
        }

        fun fail(code: String = "ERROR", message: String = "요청에 실패했습니다."): ApiResponse<Unit> {
            return ApiResponse(code = code, message = message, data = null)
        }
    }
}