package org.team_alilm.fcm.controller.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "FCM 토큰 등록 요청")
data class RegisterFcmTokenRequest(

    @Schema(
        description = "사용자의 FCM 토큰",
        example = "fCmt0kenEXAMPLE1234567890"
    )
    val fcmToken: String
)