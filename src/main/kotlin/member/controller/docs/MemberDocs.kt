package org.team_alilm.member.controller.docs

import common.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.team_alilm.common.security.CustomMemberDetails
import org.team_alilm.member.controller.dto.request.UpdateMyInfoRequest
import org.team_alilm.member.controller.dto.response.MyInfoResponse

@Tag(name = "Member", description = "회원 관련 API")
interface MemberDocs {

    @Operation(
        summary = "내 정보 조회",
        description = "인증된 사용자의 정보를 조회합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "정상 응답",
        content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = MyInfoResponse::class),
                examples = [
                    io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "ok",
                        value = """
                        {
                          "email": "examples@gmail.com",
                          "nickname": "exampleUser",
                        }
                        """
                    )
                ]
            )
        ]
    )
    fun getMyInfo(
        @Parameter(hidden = true) customMemberDetails: CustomMemberDetails
    ): ApiResponse<MyInfoResponse>

    @Operation(
        summary = "내 정보 수정",
        description = "인증된 사용자의 정보를 수정합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "정상 응답",
    )
    fun updateMyInfo(
        @Parameter(hidden = true) customMemberDetails: CustomMemberDetails,
        request: UpdateMyInfoRequest
    ): ApiResponse<Unit>
}