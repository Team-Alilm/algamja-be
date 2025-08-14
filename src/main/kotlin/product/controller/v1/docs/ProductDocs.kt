package org.team_alilm.product.controller.v1.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
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
        description = "정상 응답",
        content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProductCountResponse::class),
                examples = [
                    ExampleObject(
                        name = "ok",
                        value = """
                        {
                          "count": 12345
                        }
                        """
                    )
                ]
            )
        ]
    )
    fun getProductCount(): common.response.ApiResponse<ProductCountResponse>

    @Operation(
        summary = "상품 조회",
        description = "상품 ID에 해당하는 상품 정보를 반환합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "정상 응답",
        content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProductDetailResponse::class),
                examples = [
                    ExampleObject(
                        name = "ok",
                        value = """
                        {
                            "id": 1,
                            "name": "Sample Product",
                            "price": 10000,
                            "description": "This is a sample product.",
                            "brand": "Sample Brand",
                            "thumbnailUrl": "http://example.com/thumbnail.jpg",
                            "imageUrlList": ["http://example.com/image1.jpg", "http://example.com/image2.jpg"],
                            "store": "Sample Store",
                            "firstOption": "Size: M",
                            "secondOption": "Color: Red",
                            "thirdOption": null,
                            "waitingCount": 5
                        }
                        """
                    )
                ]
            )
        ]
    )
    fun getProductDetail(productId: Long): common.response.ApiResponse<ProductDetailResponse>

    @Operation(
        summary = "상품 목록 조회",
        description = "등록된 모든 상품의 목록을 반환합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "정상 응답",
        content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProductDetailResponse::class),
                examples = [
                    ExampleObject(
                        name = "ok",
                        value = """
                        {
                            "products": [
                                {
                                    "id": 1,
                                    "name": "Sample Product",
                                    "price": 10000,
                                    "description": "This is a sample product.",
                                    "brand": "Sample Brand",
                                    "thumbnailUrl": "http://example.com/thumbnail.jpg",
                                    "imageUrlList": ["http://example.com/image1.jpg", "http://example.com/image2.jpg"],
                                    "store": "Sample Store",
                                    "firstOption": "Size: M",
                                    "secondOption": "Color: Red",
                                    "thirdOption": null,
                                    "waitingCount": 5
                                }
                            ],
                            "hasNext": false
                        }
                        """
                    )
                ]
            )
        ]
    )
    fun getProductList(param : ProductListParam): common.response.ApiResponse<ProductListResponse>

    @Operation(
        summary = "유사 상품 조회",
        description = "유사한 상품 목록을 반환합니다. 최대 10개까지 반환됩니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "정상 응답",
        content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProductListResponse::class),
                examples = [
                    ExampleObject(
                        name = "ok",
                        value = """
                        {
                            "products": [
                                {
                                    "id": 1,
                                    "name": "Similar Product 1",
                                    "price": 12000,
                                    "brand": "Brand A",
                                    "thumbnailUrl": "http://example.com/similar1.jpg",
                                    "firstCategory": "Category A",
                                    "firstOption": "Size: L",
                                    "secondOption": null,
                                    "thirdOption": null
                                },
                                {
                                    "id": 2,
                                    "name": "Similar Product 2",
                                    "price": 15000,
                                    "brand": "Brand B",
                                    "thumbnailUrl": "http://example.com/similar2.jpg",
                                    "firstCategory": "Category B",
                                    "firstOption": null,
                                    "secondOption": null,
                                    "thirdOption": null
                                }
                            ],
                            "hasNext": false
                        }
                        """
                    )
                ]
            )
        ]
    )
    fun getSimilarProducts(productId: Long): common.response.ApiResponse<SimilarProductListResponse>

    // 최근 재 입고 상품
    @Operation(
        summary = "최근 재 입고 상품 조회",
        description = "최근 재 입고된 상품 목록을 반환합니다. 최대 10개까지 반환됩니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "정상 응답",
        content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = ProductListResponse::class),
                examples = [
                    ExampleObject(
                        name = "ok",
                        value = """
                        {
                            "products": [
                                {
                                    "id": 1,
                                    "name": "Recently Restocked Product 1",
                                    "brand": "Brand C",
                                    "thumbnailUrl": "http://example.com/restocked1.jpg",
                                },
                                {
                                    "id": 2,
                                    "name": "Recently Restocked Product 2",
                                    "brand": "Brand D",
                                    "thumbnailUrl": "http://example.com/restocked2.jpg",
                                }
                            ],
                        }
                        """
                    )
                ]
            )
        ]
    )
    fun getRecentlyRestockedProducts(): common.response.ApiResponse<RecentlyRestockedProductListResponse>

    @Operation(
        summary = "상품 등록",
        description = "새로운 상품을 등록합니다."
    )
    @ApiResponse(
        responseCode = "201",
        description = "상품이 성공적으로 등록되었습니다.",
        content = [
            Content(
                mediaType = "application/json",
                examples = [
                    ExampleObject(
                        name = "ok",
                        value = """
                        
                        """
                    )
                ]
            )
        ]
    )
    fun registerProduct(request: RegisterProductRequest): ResponseEntity<common.response.ApiResponse<Unit>>
}