package org.team_alilm.member.controller.docs

import common.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.team_alilm.common.security.CustomMemberDetails
import org.team_alilm.member.controller.dto.MyInfoResponse

@RequestMapping("/api/v2/members")
@Tag(name = "회원 API", description = "회원 관련 API 명세")
interface MemberControllerDocs {

    @Operation(
        summary = "내 정보 조회 API",
        description = "현재 로그인한 회원의 정보를 조회합니다.",
        responses = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "성공적으로 회원 정보를 조회했습니다.",
                content = [Content(schema = Schema(implementation = MyInfoResponse::class))]
            )
        ]
    )
    @GetMapping("/me")
    fun getMyInfo(
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ): ResponseEntity<ApiResponse<MyInfoResponse>>
}