package org.team_alilm.controller.product

import domain.product.Product
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.ProductRelatedUseCase
import org.team_alilm.application.port.`in`.use_case.ProductRelatedUseCase.*

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "관련 상품 API", description = """
    관련 상품을 모아둔 컨트롤러 입니다.
    해당 상품 id 값을 보내주면 알려드릴게요.
    최대 10개를 반환 합니다.
""")
class ProductRelatedController(
    private val productRelatedUseCase: ProductRelatedUseCase
) {

    @Operation(
        summary = "관련 상품 조회 API",
    )
    @GetMapping("/related/{productId}")
    fun productRecent(@PathVariable productId: Long) : ResponseEntity<org.team_alilm.controller.product.ProductRelatedController.ProductRelatedResponse> {
        val command = ProductRelatedCommand(productId = productId)
        val result = productRelatedUseCase.productRelated(command)
        val response =
            org.team_alilm.controller.product.ProductRelatedController.ProductRelatedResponse.Companion.from(result)

        return ResponseEntity.ok(response)
    }

    data class ProductRelatedResponse(
        val relatedProductList: List<org.team_alilm.controller.product.ProductRelatedController.RelatedProduct>
    ) {
        companion object {
            fun from(result: ProductRelatedResult): org.team_alilm.controller.product.ProductRelatedController.ProductRelatedResponse {
                return org.team_alilm.controller.product.ProductRelatedController.ProductRelatedResponse(
                    relatedProductList = result.productList.map {
                        org.team_alilm.controller.product.ProductRelatedController.RelatedProduct.Companion.from(
                            it
                        )
                    }
                )
            }
        }
    }

    data class RelatedProduct(
        val id: Long,
        val name: String,
        val imageUrl: String,
        val brand: String,
        val price: Int,
        val firstCategory: String,
        val firstOption: String?,
        val secondOption: String?,
        val thirdOption: String?
    ) {
        companion object {
            fun from(product: Product): org.team_alilm.controller.product.ProductRelatedController.RelatedProduct {
                return org.team_alilm.controller.product.ProductRelatedController.RelatedProduct(
                    id = product.id?.value ?: 0,
                    name = product.name,
                    imageUrl = product.thumbnailUrl,
                    brand = product.brand,
                    price = product.price,
                    firstCategory = product.firstCategory,
                    firstOption = product.firstOption,
                    secondOption = product.secondOption,
                    thirdOption = product.thirdOption
                )
            }
        }
    }
}