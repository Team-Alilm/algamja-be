package org.team_alilm.product.controller

import common.response.ApiResponse
import common.response.ApiResponse.Companion.success
import org.springdoc.core.annotations.ParameterObject
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.product.controller.docs.ProductDocs
import org.team_alilm.product.controller.dto.param.ProductListParam
import org.team_alilm.product.controller.dto.response.ProductCountResponse
import org.team_alilm.product.controller.dto.response.ProductDetailResponse
import org.team_alilm.product.controller.dto.response.ProductListResponse
import org.team_alilm.product.controller.dto.response.RecentlyRestockedProductListResponse
import org.team_alilm.product.controller.dto.response.SimilarProductListResponse
import org.team_alilm.product.service.ProductService

@RestController
@RequestMapping("/api/v1/products")
class ProductController(

    private val productService: ProductService
) : ProductDocs {

    @GetMapping("/api/v1/products/count")
    override fun getProductCount(): ApiResponse<ProductCountResponse> {
        return success(data = productService.getProductCount())
    }

    @GetMapping("/{productId}")
    override fun getProductDetail(
        @PathVariable("productId") productId: Long
    ): ApiResponse<ProductDetailResponse> {
        val response = productService.getProductDetail(productId)
        return success(data = response)
    }

    @GetMapping
    override fun getProductList(
        @ParameterObject param : ProductListParam
    ): ApiResponse<ProductListResponse>{
        val response = productService.getProductList(param)
        return success(data = response)
    }

    @GetMapping("/similar/{productId}")
    override fun getSimilarProducts(
        @PathVariable("productId") productId: Long
    ): ApiResponse<SimilarProductListResponse> {
        val response = productService.getSimilarProducts(productId)
        return success(data = response)
    }

    @GetMapping("/recently-restocked")
    override fun getRecentlyRestockedProducts(): ApiResponse<RecentlyRestockedProductListResponse> {
        val response = productService.getRecentlyRestockedProducts()
        return success(data = response)
    }
}