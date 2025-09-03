package org.team_alilm.algamja.product.controller.v1

import org.team_alilm.algamja.common.response.ApiResponse
import org.team_alilm.algamja.common.response.ApiResponse.Companion.success
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.algamja.common.security.CustomMemberDetails
import org.team_alilm.algamja.common.security.requireMemberId
import org.team_alilm.algamja.product.controller.v1.docs.ProductDocs
import org.team_alilm.algamja.product.controller.v1.dto.param.ProductListParam
import org.team_alilm.algamja.product.controller.v1.dto.param.ProductCountParam
import org.team_alilm.algamja.product.controller.v1.dto.request.ProductRegisterRequest
import org.team_alilm.algamja.product.controller.v1.dto.response.CrawlProductResponse
import org.team_alilm.algamja.product.controller.v1.dto.response.ProductRegisterResponse
import org.team_alilm.algamja.product.controller.v1.dto.response.ProductCountResponse
import org.team_alilm.algamja.product.controller.v1.dto.response.ProductDetailResponse
import org.team_alilm.algamja.product.controller.v1.dto.response.ProductListResponse
import org.team_alilm.algamja.product.controller.v1.dto.response.RecentlyRestockedProductListResponse
import org.team_alilm.algamja.product.controller.v1.dto.response.SimilarProductListResponse
import org.team_alilm.algamja.product.service.ProductService

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

    @GetMapping("/count")
    override fun getProductCount(
        @ParameterObject @Valid param : ProductCountParam
    ): ApiResponse<ProductCountResponse> {
        return success(
            data = productService.getProductCount(param)
        )
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

    @GetMapping("/crawl")
    override fun crawlProduct(
        @RequestParam("productUrl") productUrl: String
    ): ApiResponse<CrawlProductResponse> {
        return success(data = productService.crawlProduct(productUrl))
    }

//    @GetMapping("/delayed/me")
//    override fun getMostDelayedProductByMember(
//        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
//    ): ApiResponse<DelayedProductResponse?> {
//        val response = productService.getMostDelayedProductByMember(customMemberDetails.requireMemberId())
//        return success(data = response)
//    }

    @PostMapping
    override fun registerProduct(
        @RequestBody @Valid request: ProductRegisterRequest,
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ): ApiResponse<ProductRegisterResponse> {
        val response = productService.registerProduct(request, customMemberDetails.requireMemberId())
        return success(data = response)
    }
}