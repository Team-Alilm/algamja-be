package org.team_alilm.member.controller.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "내 정보 응답")
data class MyInfoResponse(

    @Schema(description = "회원 ID", example = "1")
    val id: Long,

    @Schema(description = "회원 이메일", example = "hong@example.com")
    val email: String,

    @Schema(description = "회원 닉네임", example = "홍길동")
    val nickname: String,

    @Schema(description = "회원 가입 경로", example = "KAKAO")
    val provider: String
)