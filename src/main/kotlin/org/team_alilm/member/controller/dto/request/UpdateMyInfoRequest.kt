package org.team_alilm.member.controller.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "내 정보 수정 요청")
data class UpdateMyInfoRequest(

    @field:NotBlank(message = "닉네임은 필수입니다.")
    @field:Size(max = 10, message = "닉네임은 최대 10자까지 가능합니다.")
    @Schema(description = "닉네임", example = "홍길동")
    val nickname: String,

    @field:NotBlank(message = "이메일은 필수입니다.")
    @field:Email(message = "유효한 이메일 형식이어야 합니다.")
    @Schema(description = "이메일", example = "hong@example.com")
    val email: String
)