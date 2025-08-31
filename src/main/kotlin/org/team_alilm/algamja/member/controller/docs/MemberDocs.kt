package org.team_alilm.algamja.member.controller.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.team_alilm.algamja.common.response.ApiResponse as CommonApiResponse
import org.team_alilm.algamja.common.security.CustomMemberDetails
import org.team_alilm.algamja.member.controller.dto.request.UpdateMyInfoRequest
import org.team_alilm.algamja.member.controller.dto.response.MyInfoResponse
import org.team_alilm.algamja.member.controller.dto.response.UserStatisticsResponse

@Tag(name = "Member", description = "회원 관련 API")
interface MemberDocs {

    @Operation(
        summary = "내 정보 조회",
        description = "인증된 사용자의 정보를 조회합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "정상 응답"
    )
    fun getMyInfo(
        @Parameter(hidden = true) customMemberDetails: CustomMemberDetails
    ): CommonApiResponse<MyInfoResponse>

    @Operation(
        summary = "내 정보 수정",
        description = "인증된 사용자의 정보를 수정합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "정상 응답",
    )
    fun updateMyInfo(
        @Parameter(hidden = true) customMemberDetails: CustomMemberDetails,
        request: UpdateMyInfoRequest
    ): CommonApiResponse<Unit>

    @Operation(
        summary = "내 통계 조회",
        description = "인증된 사용자의 등록 상품 수와 받은 알림 수를 조회합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "정상 응답"
    )
    fun getMyStatistics(
        @Parameter(hidden = true) customMemberDetails: CustomMemberDetails
    ): CommonApiResponse<UserStatisticsResponse>
}