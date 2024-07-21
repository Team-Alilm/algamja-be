package org.teamalilm.alilmbe.adapter.`in`.web.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.teamalilm.alilmbe.adapter.`in`.web.controller.request.ProductCrawlingRequest
import org.teamalilm.alilmbe.adapter.`in`.web.controller.response.ProductCrawlingResponse
import org.teamalilm.alilmbe.application.port.`in`.use_case.ProductCrawlingUseCase
import org.teamalilm.alilmbe.service.crawling.ProductCrawlingService
import org.teamalilm.alilmbe.web.adapter.error.RequestValidateException

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "product-crawling", description = "상품 크롤링 API")
class ProductCrawlingController(
    private val productCrawlingUseCase: ProductCrawlingUseCase
) {

    @Operation(
        summary = "상품 크롤링을 실행하는 API",
        description = """
            사용자가 url을 입력하면 해당 상품의 데이터를 파싱하는 API에요.
            상품 크롤링을 실행하는 API 이며, 크롤링된 상품은 DB에 저장되어요.
        """
    )
    @GetMapping("/scraping")
    fun crawling(
        @ParameterObject
        @Valid
        request: ProductCrawlingRequest,

        bindingResult: BindingResult
    ): ResponseEntity<ProductCrawlingResponse> {
        if (bindingResult.hasErrors()) {
            throw RequestValidateException(bindingResult)
        }

        val command = ProductCrawlingService.ProductCrawlingCommand(
            url = request.url
        )

        val result = productCrawlingUseCase.invoke(command)

        val response = ProductCrawlingResponse(
            name = result.name,
            brand = result.brand,
            imageUrl = result.imageUrl,
            category = result.category,
            price = result.price,
            store = result.store,
            option1 = result.option1List,
            option2 = result.option2List,
            option3 = result.option3List
        )

        return ResponseEntity.ok(response)
    }

}