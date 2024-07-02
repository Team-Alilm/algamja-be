package org.teamalilm.alilmbe.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.teamalilm.alilmbe.controller.error.RequestValidateException
import org.teamalilm.alilmbe.domain.product.entity.Product
import org.teamalilm.alilmbe.service.ProductCrawlingService

@RestController
@RequestMapping("/api/v1/product-crawling")
@Tag(name = "product-crawling", description = "상품 크롤링 API")
class ProductCrawlingController(
    private val productCrawlingService: ProductCrawlingService
) {

    @Operation(
        summary = "상품 크롤링을 실행하는 API",
        description = """
            상품 크롤링을 실행하는 API 이며, 크롤링된 상품은 DB에 저장되어요.
        """
    )
    @GetMapping
    fun crawling(
        @ParameterObject
        @Valid
        requestBody: ProductCrawlingRequestBody,

        bindingResult: BindingResult
    ): ResponseEntity<ProductCrawlingResponseBody> {
        if (bindingResult.hasErrors()) {
            throw RequestValidateException(bindingResult)
        }

        val command = ProductCrawlingService.ProductCrawlingCommand(
            url = requestBody.url
        )

        val result = productCrawlingService.crawling(command)

        val response = ProductCrawlingResponseBody(
            name = result.name,
            imageUrl = result.imageUrl,
            price = result.price,
            store = result.store,
            option1 = result.option1,
            option2 = result.option2,
            option3 = result.option3
        )

        return ResponseEntity.ok(response)
    }

    data class ProductCrawlingRequestBody (
        @NotBlank(message = "URL은 필수에요.")
        @Schema(description = "크롤링할 상품 URL", defaultValue = "https://www.musinsa.com/app/goods/3262292")
        val url: String
    )

    data class ProductCrawlingResponseBody (
        val name: String,
        val imageUrl: String,
        val price: Int,
        val store: Product.ProductInfo.Store,
        val option1: String,
        val option2: String?,
        val option3: String?
    )
}