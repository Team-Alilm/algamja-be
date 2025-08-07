package common.response

import org.springframework.http.ResponseEntity

data class ApiResponse<T>(
    val code: String,
    val message: String,
    val data: T? = null
) {
    companion object {

        fun <T> success(
            data: T, message: String = "요청이 성공적으로 처리되었습니다"
        ): ResponseEntity<ApiResponse<T>> {
            return ResponseEntity.ok(
                ApiResponse(
                    code = "0000", message = message, data = data
                )
            )
        }
    }
}
