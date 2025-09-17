package org.team_alilm.algamja.banner.controller.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.team_alilm.algamja.banner.controller.dto.response.BannerListResponse
import org.team_alilm.algamja.common.response.ApiResponse as CommonApiResponse

@Tag(name = "배너", description = "배너 관련 API")
interface BannerDocs {

    @Operation(
        summary = "활성 배너 목록 조회",
        description = "현재 시점에서 활성화된 배너 목록을 우선순위 순으로 조회합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "배너 목록 조회 성공")
        ]
    )
    fun getActiveBanners(): CommonApiResponse<BannerListResponse>
}