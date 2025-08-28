package org.team_alilm.algamja.fcm.controller

import org.team_alilm.algamja.common.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.algamja.common.security.CustomMemberDetails
import org.team_alilm.algamja.common.security.requireMemberId
import org.team_alilm.algamja.fcm.controller.docs.FcmTokenDocs
import org.team_alilm.algamja.fcm.controller.dto.request.RegisterFcmTokenRequest
import org.team_alilm.algamja.fcm.service.FcmTokenService

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
            memberId = customMemberDetails.requireMemberId(),
            fcmToken = request.fcmToken
        )

        return ApiResponse.success(Unit)
    }
}