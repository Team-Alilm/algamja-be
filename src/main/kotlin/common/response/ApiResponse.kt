package common.response

import org.springframework.http.ResponseEntity
import org.team_alilm.notification.controller.v1.NotificationControllerV1

data class ApiResponse<T>(
    val code: String,
    val message: String,
    val data: T? = null
) {
    companion object {

        fun <T> success(
            data: T, message: String = "요청이 성공적으로 처리되었습니다"
        ): ApiResponse<NotificationControllerV1.UnreadNotificationCountResponse> {
            return ResponseEntity.ok(
                ApiResponse(
                    code = "0000", message = message, data = data
                )
            )
        }
    }
}
