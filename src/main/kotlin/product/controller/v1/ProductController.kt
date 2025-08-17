package org.team_alilm.product.controller.v1

import common.response.ApiResponse
import common.response.ApiResponse.Companion.success
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.product.controller.v1.docs.ProductDocs
import org.team_alilm.product.controller.v1.dto.param.ProductListParam
import org.team_alilm.product.controller.v1.dto.request.RegisterProductRequest
import org.team_alilm.product.controller.v1.dto.response.ProductCountResponse
import org.team_alilm.product.controller.v1.dto.response.ProductDetailResponse
import org.team_alilm.product.controller.v1.dto.response.ProductListResponse
import org.team_alilm.product.controller.v1.dto.response.RecentlyRestockedProductListResponse
import org.team_alilm.product.controller.v1.dto.response.SimilarProductListResponse
import org.team_alilm.product.service.ProductService

@RestController
@RequestMapping("/api/v1/products")
class ProductController(

    private val productService: ProductService
) : ProductDocs {

    @GetMapping
    override fun getProductList(
        @ParameterObject @Valid param : ProductListParam
    ): ApiResponse<ProductListResponse>{
        val response = productService.getProductList(param)
        return success(data = response)
    }

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

    @PostMapping
    override fun registerProduct(
        @RequestBody @Valid request: RegisterProductRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        productService.registerProduct(request)

        return ApiResponse.created(data = Unit)
    }
}