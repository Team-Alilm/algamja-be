package org.team_alilm.product.controller

import common.response.ApiResponse
import common.response.ApiResponse.Companion.success
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.product.controller.docs.ProductDocs
import org.team_alilm.product.controller.dto.ProductCountResponse
import org.team_alilm.product.controller.dto.ProductDetailResponse
import org.team_alilm.product.service.ProductService

@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private val productService: ProductService
) : ProductDocs {

    @GetMapping("/api/v1/products/count")
    override fun getProductCount(): ApiResponse<ProductCountResponse> {
        return success(
            data = productService.getProductCount()
        )
    }

    @GetMapping("{productId}")
    override fun getProductDetail(productId: Long): ApiResponse<ProductDetailResponse> {
        val response = productService.getProductDetail(productId)
        return success(
            data = response
        )
    }
}