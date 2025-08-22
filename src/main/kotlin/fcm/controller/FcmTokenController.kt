package org.team_alilm.fcm.controller

import common.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.common.security.CustomMemberDetails
import org.team_alilm.fcm.controller.docs.FcmTokenDocs
import org.team_alilm.fcm.controller.dto.request.RegisterFcmTokenRequest
import org.team_alilm.fcm.service.FcmTokenService

@RestController
@RequestMapping("/api/v1/fcms")
class FcmTokenController(
    private val fcmTokenService: FcmTokenService
) : FcmTokenDocs {

    @PostMapping
    override fun registerFcmToken(
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails,
        @RequestBody @Valid request: RegisterFcmTokenRequest
    ) : ApiResponse<Unit> {

        fcmTokenService.registerFcmToken(
            memberId = customMemberDetails.memberRow.id!!,
            fcmToken = request.fcmToken
        )

        return ApiResponse.success(data = Unit)
    }
}