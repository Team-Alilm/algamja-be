package org.team_alilm.product.controller.v1.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.team_alilm.product.controller.v1.dto.param.ProductListParam
import org.team_alilm.product.controller.v1.dto.request.RegisterProductRequest
import org.team_alilm.product.controller.v1.dto.response.ProductCountResponse
import org.team_alilm.product.controller.v1.dto.response.ProductDetailResponse
import org.team_alilm.product.controller.v1.dto.response.ProductListResponse
import org.team_alilm.product.controller.v1.dto.response.RecentlyRestockedProductListResponse
import org.team_alilm.product.controller.v1.dto.response.SimilarProductListResponse

@Tag(name = "Product", description = "상품 관련 API")
interface ProductDocs {

    @Operation(
        summary = "상품 총 개수 조회",
        description = "등록된 모든 상품의 총 개수를 반환합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "정상 응답"
    )
    fun getProductCount(
        param : ProductListParam
    ): common.response.ApiResponse<ProductCountResponse>

    @Operation(
        summary = "상품 조회",
        description = "상품 ID에 해당하는 상품 정보를 반환합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "정상 응답"
    )
    fun getProductDetail(productId: Long): common.response.ApiResponse<ProductDetailResponse>

    @Operation(
        summary = "상품 목록 조회",
        description = "등록된 모든 상품의 목록을 반환합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "정상 응답"
    )
    fun getProductList(param : ProductListParam): common.response.ApiResponse<ProductListResponse>

    @Operation(
        summary = "유사 상품 조회",
        description = "유사한 상품 목록을 반환합니다. 최대 10개까지 반환됩니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "정상 응답"
    )
    fun getSimilarProducts(productId: Long): common.response.ApiResponse<SimilarProductListResponse>

    // 최근 재 입고 상품
    @Operation(
        summary = "최근 재 입고 상품 조회",
        description = "최근 재 입고된 상품 목록을 반환합니다. 최대 10개까지 반환됩니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "정상 응답"
    )
    fun getRecentlyRestockedProducts(): common.response.ApiResponse<RecentlyRestockedProductListResponse>

    @Operation(
        summary = "상품 등록",
        description = "새로운 상품을 등록합니다."
    )
    @ApiResponse(
        responseCode = "201",
        description = "상품이 성공적으로 등록되었습니다."
    )
    fun registerProduct(request: RegisterProductRequest): ResponseEntity<common.response.ApiResponse<Unit>>
}