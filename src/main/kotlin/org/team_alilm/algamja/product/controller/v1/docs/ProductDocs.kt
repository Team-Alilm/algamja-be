package org.team_alilm.algamja.product.controller.v1.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.team_alilm.algamja.common.response.ApiResponse as CommonApiResponse
import org.team_alilm.algamja.common.security.CustomMemberDetails
import org.team_alilm.algamja.product.controller.v1.dto.param.ProductListParam
import org.team_alilm.algamja.product.controller.v1.dto.request.ProductRegisterRequest
import org.team_alilm.algamja.product.controller.v1.dto.response.CrawlProductResponse
import org.team_alilm.algamja.product.controller.v1.dto.response.DelayedProductResponse
import org.team_alilm.algamja.product.controller.v1.dto.response.ProductRegisterResponse
import org.team_alilm.algamja.product.controller.v1.dto.response.ProductCountResponse
import org.team_alilm.algamja.product.controller.v1.dto.response.ProductDetailResponse
import org.team_alilm.algamja.product.controller.v1.dto.response.ProductListResponse
import org.team_alilm.algamja.product.controller.v1.dto.response.RecentlyRestockedProductListResponse
import org.team_alilm.algamja.product.controller.v1.dto.response.SimilarProductListResponse

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
    ): CommonApiResponse<ProductCountResponse>

    @Operation(
        summary = "상품 조회",
        description = "상품 ID에 해당하는 상품 정보를 반환합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "정상 응답"
    )
    fun getProductDetail(productId: Long): CommonApiResponse<ProductDetailResponse>

    @Operation(
        summary = "상품 목록 조회",
        description = "등록된 모든 상품의 목록을 반환합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "정상 응답"
    )
    fun getProductList(param : ProductListParam): CommonApiResponse<ProductListResponse>

    @Operation(
        summary = "유사 상품 조회",
        description = "유사한 상품 목록을 반환합니다. 최대 10개까지 반환됩니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "정상 응답"
    )
    fun getSimilarProducts(productId: Long): CommonApiResponse<SimilarProductListResponse>

    // 최근 재 입고 상품
    @Operation(
        summary = "최근 재 입고 상품 조회",
        description = "최근 재 입고된 상품 목록을 반환합니다. 최대 10개까지 반환됩니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "정상 응답"
    )
    fun getRecentlyRestockedProducts(): CommonApiResponse<RecentlyRestockedProductListResponse>

    @Operation(
        summary = "상품 조회 (크롤링)",
        description = "새로운 상품을 등록합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "상품이 성공적으로 등록되었습니다."
    )
    fun crawlProduct(productUrl: String): CommonApiResponse<CrawlProductResponse>

    @Operation(
        summary = "재입고 지연 상품 조회",
        description = "회원의 장바구니에서 재입고가 가장 오래 지연된 상품을 조회합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "정상 응답"
    )
    fun getMostDelayedProductByMember(
        @Parameter(hidden = true) customMemberDetails: CustomMemberDetails
    ): CommonApiResponse<DelayedProductResponse?>

    @Operation(
        summary = "상품 등록",
        description = "크롤링된 상품 정보를 받아서 새로운 상품을 등록합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "상품이 성공적으로 등록되었습니다."
    )
    fun registerProduct(request: ProductRegisterRequest): CommonApiResponse<ProductRegisterResponse>
}