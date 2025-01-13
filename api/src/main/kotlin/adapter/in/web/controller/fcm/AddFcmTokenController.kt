package org.team_alilm.adapter.`in`.web.controller.fcm

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
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
        @RequestBody @Valid request: FcmTokenRequest,
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
        @field:NotBlank(message = "FCM 토큰은 필수입니다.")
        @JsonProperty("fcmToken")
        val fcmToken: String
    )
}
