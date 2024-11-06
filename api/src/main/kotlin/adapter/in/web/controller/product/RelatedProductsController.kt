package org.team_alilm.adapter.`in`.web.controller.product

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.RelatedProductsUseCase
import org.team_alilm.application.port.`in`.use_case.RelatedProductsUseCase.*

@RestController
@Tag(name = "관련 상품 조회 API", description = """
    관련 상품을 조회 합니다.
""")
@RequestMapping("/api/v1/products")
class RelatedProductsController(
    private val relatedProductsUseCase: RelatedProductsUseCase
) {

    @GetMapping("/related")
    fun relatedProducts(
        productId: Long
    ) : ResponseEntity<RelatedProductsResponse> {
        val command = RelatedProductsCommand(productId = productId)
        val result = relatedProductsUseCase.relatedProducts(command = command)
        val response = RelatedProductsResponse.from(result)
        return ResponseEntity.ok(response)
    }

    data class RelatedProductsResponse(
        val id: Long,
        val number: Long,
        val name: String,
        val brand: String,
        val imageUrl: String,
        val store: String,
        val price: Int,
        val category: String,
        val firstOption: String,
        val secondOption: String?,
        val thirdOption: String?,
    ) {

        companion object {
            fun from(relatedProductsResult : RelatedProductsResult): RelatedProductsResponse {
                return RelatedProductsResponse(
                    id = relatedProductsResult.id,
                    number = relatedProductsResult.number,
                    name = relatedProductsResult.name,
                    brand = relatedProductsResult.brand,
                    imageUrl = relatedProductsResult.imageUrl,
                    store = relatedProductsResult.store,
                    price = relatedProductsResult.price,
                    category = relatedProductsResult.category,
                    firstOption = relatedProductsResult.firstOption,
                    secondOption = relatedProductsResult.secondOption,
                    thirdOption = relatedProductsResult.thirdOption
                )
            }
        }
    }
}