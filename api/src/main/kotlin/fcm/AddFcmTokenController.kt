package org.team_alilm.fcm

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.AddFcmTokenUseCase
import org.team_alilm.data.CustomMemberDetails

@RestController
@RequestMapping("/api/v1/fcm-tokens")
class AddFcmTokenController(
    private val addFcmTokenUseCase: AddFcmTokenUseCase
) {

    @PostMapping
    fun addFcmToken(
        @RequestBody request: org.team_alilm.controller.fcm.AddFcmTokenController.FcmTokenRequest,
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ) : ResponseEntity<Unit> {
        val command = AddFcmTokenUseCase.AddFcmTokenCommand(
            token = request.fcmToken,
            member = customMemberDetails.member
        )

        addFcmTokenUseCase.addFcmToken(command)

        return ResponseEntity.ok().build()
    }

    @Schema(description = "FCM 토큰 등록 요청")
    data class FcmTokenRequest(
        val fcmToken: String,
        val os: String? = null
    )
}
