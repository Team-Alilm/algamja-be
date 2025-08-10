package org.team_alilm.product.controller.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.team_alilm.product.controller.dto.ProductCountResponse
import org.team_alilm.product.controller.dto.ProductDetailResponse

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
                          "description": "This is a sample product."
                        }
                        """
                    )
                ]
            )
        ]
    )
    fun getProductDetail(productId: Long): common.response.ApiResponse<ProductDetailResponse>

}