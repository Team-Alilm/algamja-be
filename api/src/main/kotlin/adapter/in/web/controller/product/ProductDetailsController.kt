package org.team_alilm.adapter.`in`.web.controller.product

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.ProductDetailsUseCase
import org.team_alilm.application.port.`in`.use_case.ProductDetailsUseCase.*

@RestController
@Tag(name = "상품 상세 조회 API", description = "상품 상세 조회 API를 제공합니다.")
@RequestMapping("/api/v1/products")
class ProductDetailsController(
    private val productDetailsUseCase: ProductDetailsUseCase
) {

    @GetMapping("/{productId}")
    fun productDetails(
        @PathVariable
        productId: Long
    ) : ResponseEntity<ProductDetailsResponse> {
        val command = ProductDetailsCommand(productId = productId)
        val result = productDetailsUseCase.productDetails(command = command)
        val response = ProductDetailsResponse.from(result)

        return ResponseEntity.ok(response)
    }

    data class ProductDetailsResponse(
        val id: Long,
        val number: Long,
        val name: String,
        val brand: String,
        val thumbnailUrl: String,
        val imageList: List<String>,
        val store: String,
        val price: Int,
        val category: String,
        val firstOption: String,
        val secondOption: String?,
        val thirdOption: String?,
        val waitingCount: Long
    ) {

        companion object {
            fun from (productDetails: ProductDetailsResult): ProductDetailsResponse {
                return ProductDetailsResponse(
                    id = productDetails.id,
                    number = productDetails.number,
                    name = productDetails.name,
                    brand = productDetails.brand,
                    thumbnailUrl = productDetails.thumbnailUrl,
                    imageList = productDetails.imageList,
                    store = productDetails.store,
                    price = productDetails.price,
                    category = productDetails.category,
                    firstOption = productDetails.firstOption,
                    secondOption = productDetails.secondOption,
                    thirdOption = productDetails.thirdOption,
                    waitingCount = productDetails.waitingCount
                )
            }
        }
    }
}
