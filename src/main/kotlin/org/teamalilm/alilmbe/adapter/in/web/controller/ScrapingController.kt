package org.teamalilm.alilmbe.adapter.`in`.web.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.teamalilm.alilmbe.domain.product.entity.Product.ProductInfo.*
import org.teamalilm.alilmbe.service.crawling.ProductCrawlingService

@Tag(name = "scraping", description = "Scraping APIs")
class ScrapingController(
    private val productCrawlingService: ProductCrawlingService
) {

    @Operation(
        summary = "Scraping API",
        description = """
            사용자의 URL을 받아서 상품의 정보를 추출하는 API 입니다. 
            
            저희 서비스에 데이터를 저장하는 로직이 1도 없습니다.
        """
    )
    @GetMapping("/scraping")
    fun scraping(
        @RequestBody @Valid request: ScrapingRequestBody,
        bindingResult: BindingResult
    ): ResponseEntity<ScrapingResponse> {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build()
        }

        val command = ProductCrawlingService.ProductCrawlingCommand(
            url = request.url
        )

        val result = productCrawlingService.crawling(command)

        val response = ScrapingResponse(
            name = result.name,
            brand = result.brand,
            imageUrl = result.imageUrl,
            category = result.category,
            price = result.price,
            store = result.store,
            option1s = result.option1List,
            option2s = result.option2List,
            option3s = result.option3List
        )

        return ResponseEntity.ok(response)
    }

    @Schema(description = "Scraping 요청")
    data class ScrapingRequestBody(
        @NotBlank
        @Schema(description = "상품 URL", example = "https://www.musinsa.com/app/goods/3262292")
        val url: String
    )

    @Schema(description = "Alilm 등록을 위한 요청 DTO")
    data class ScrapingResponse(
        private val name: String,
        private val brand: String,
        private val imageUrl: String,
        private val category: String,
        private val price: Int,
        private val store: Store,
        private val option1s: List<String>,
        private val option2s: List<String>,
        private val option3s: List<String>
    )

}