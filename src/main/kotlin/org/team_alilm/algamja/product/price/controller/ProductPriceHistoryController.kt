package org.team_alilm.algamja.product.price.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.team_alilm.algamja.common.response.ApiResponse
import org.team_alilm.algamja.common.security.CustomMemberDetails
import org.team_alilm.algamja.product.price.controller.docs.ProductPriceHistoryDocs
import org.team_alilm.algamja.product.price.service.PriceHistoryDto
import org.team_alilm.algamja.product.price.service.PriceStatsDto
import org.team_alilm.algamja.product.price.service.ProductPriceHistoryService

@RestController
@RequestMapping("/api/v1/products")
class ProductPriceHistoryController(
    private val productPriceHistoryService: ProductPriceHistoryService
) : ProductPriceHistoryDocs {

    @GetMapping("/{productId}/price-history")
    override fun getPriceHistory(
        @PathVariable productId: Long,
        @RequestParam(defaultValue = "30") limit: Int,
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ): ApiResponse<List<PriceHistoryDto>> {
        val priceHistory = productPriceHistoryService.getPriceHistory(productId, limit)
        return ApiResponse.success(priceHistory)
    }

    @GetMapping("/{productId}/price-history/period")
    override fun getPriceHistoryByPeriod(
        @PathVariable productId: Long,
        @RequestParam startTime: Long,
        @RequestParam endTime: Long,
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ): ApiResponse<List<PriceHistoryDto>> {
        val priceHistory = productPriceHistoryService.getPriceHistoryByPeriod(productId, startTime, endTime)
        return ApiResponse.success(priceHistory)
    }

    @GetMapping("/{productId}/price-stats")
    override fun getPriceStats(
        @PathVariable productId: Long,
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ): ApiResponse<PriceStatsDto> {
        val priceStats = productPriceHistoryService.getPriceStats(productId)
        return ApiResponse.success(priceStats)
    }
}