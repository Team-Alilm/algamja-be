package org.team_alilm.algamja.product.price.controller.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.team_alilm.algamja.common.response.ApiResponse as CommonApiResponse
import org.team_alilm.algamja.common.security.CustomMemberDetails
import org.team_alilm.algamja.product.price.service.PriceHistoryDto
import org.team_alilm.algamja.product.price.service.PriceStatsDto

@Tag(name = "Product Price History", description = "상품 가격 히스토리 관련 API")
interface ProductPriceHistoryDocs {

    @Operation(
        summary = "상품 가격 히스토리 조회",
        description = "특정 상품의 가격 변동 히스토리를 최신순으로 조회합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "정상 응답"
    )
    fun getPriceHistory(
        @Parameter(description = "상품 ID") productId: Long,
        @Parameter(description = "조회할 히스토리 개수", example = "30") limit: Int,
        @Parameter(hidden = true) customMemberDetails: CustomMemberDetails
    ): CommonApiResponse<List<PriceHistoryDto>>

    @Operation(
        summary = "기간별 상품 가격 히스토리 조회",
        description = "특정 기간의 상품 가격 변동 히스토리를 조회합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "정상 응답"
    )
    fun getPriceHistoryByPeriod(
        @Parameter(description = "상품 ID") productId: Long,
        @Parameter(description = "시작 시간 (timestamp)", example = "1640995200000") startTime: Long,
        @Parameter(description = "종료 시간 (timestamp)", example = "1672531200000") endTime: Long,
        @Parameter(hidden = true) customMemberDetails: CustomMemberDetails
    ): CommonApiResponse<List<PriceHistoryDto>>

    @Operation(
        summary = "상품 가격 통계 조회",
        description = "상품의 가격 변동 통계 정보를 조회합니다. (최저가, 최고가, 평균가, 변동 횟수 등)"
    )
    @ApiResponse(
        responseCode = "200",
        description = "정상 응답"
    )
    fun getPriceStats(
        @Parameter(description = "상품 ID") productId: Long,
        @Parameter(hidden = true) customMemberDetails: CustomMemberDetails
    ): CommonApiResponse<PriceStatsDto>
}