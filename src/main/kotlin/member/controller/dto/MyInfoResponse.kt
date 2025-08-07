package org.team_alilm.member.controller.dto

import io.swagger.v3.oas.annotations.media.Schema

data class MyInfoResponse(
    @Schema(description = "회원 이메일") val email: String,
    @Schema(description = "회원 이름") val name: String
)