package org.team_alilm.product

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.ProductCategoryUseCase

@RestController
@Tag(name = "상품 카테고리 조회 API", description = "상품 카테고리 조회 API를 제공합니다.")
@RequestMapping("/api/v1/products/category")
class ProductCategoryController(
    private val productCategoryUseCase: ProductCategoryUseCase,
) {

    @Operation(
        summary = "상품 카테고리 조회 API",
        description = """
            상품 카테고리를 조회할 수 있는 API를 제공합니다.
    """
    )
    @GetMapping
    fun productCategory() : ResponseEntity<ProductCategoryResponse> {
        val result = productCategoryUseCase.productCategory()

        return ResponseEntity.ok(ProductCategoryResponse(result))
    }

    data class ProductCategoryResponse(
        val productCategoryList: List<String>
    )
}