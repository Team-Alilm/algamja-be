package org.team_alilm.fcm.controller.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "FCM 토큰 등록 요청")
data class RegisterFcmTokenRequest(

    @field:NotBlank(message = "FCM 토큰은 필수입니다.")
    @field:Size(min = 10, max = 500, message = "FCM 토큰은 10자 이상 500자 이하여야 합니다.")
    @Schema(
        description = "사용자의 FCM 토큰",
        example = "fCmt0kenEXAMPLE1234567890"
    )
    val fcmToken: String
)