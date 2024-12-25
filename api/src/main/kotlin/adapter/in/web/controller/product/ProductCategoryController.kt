package org.team_alilm.adapter.`in`.web.controller.product

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.ProductCategoryUseCase

@RestController
@RequestMapping("/api/v1/products/category")
class ProductCategoryController(
    private val productCategoryUseCase: ProductCategoryUseCase,
) {

    @GetMapping
    fun productCategory() : ResponseEntity<ProductCategoryResponse> {
        val result = productCategoryUseCase.productCategory()

        return ResponseEntity.ok(ProductCategoryResponse(result))
    }

    data class ProductCategoryResponse(
        val productCategoryList: List<String>
    )
}