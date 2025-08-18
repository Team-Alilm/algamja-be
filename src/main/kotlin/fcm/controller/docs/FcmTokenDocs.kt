package org.team_alilm.fcm.controller.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "FcmToken", description = "FCM 토큰 관련 API")
interface FcmTokenDocs {

    @Operation(
        summary = "FCM 토큰 등록",
        description = "사용자의 FCM 토큰을 등록합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "정상 응답"
    )
    fun registerFcmToken(
        @Parameter(hidden = true) customMemberDetails: org.team_alilm.common.security.CustomMemberDetails,
        request: org.team_alilm.fcm.controller.dto.request.RegisterFcmTokenRequest
    ) : common.response.ApiResponse<Unit>
}