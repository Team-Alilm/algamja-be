package org.team_alilm.adapter.`in`.web.controller.product

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.team_alilm.application.port.`in`.use_case.product.crawling.ProductCrawlingUseCase
import org.team_alilm.application.port.`in`.use_case.product.crawling.ProductCrawlingUseCaseResolver
import org.team_alilm.domain.product.Store
import org.team_alilm.global.error.NotFoundStoreException

@RestController
@Tag(name = "상품 크롤링 조회 API", description = "상품 크롤링 조회 API를 제공합니다.")
@RequestMapping("/api/v1/products")
class ProductCrawlingController(
    private val productCrawlingUseCaseResolver: ProductCrawlingUseCaseResolver,
    private val restTemplate: RestTemplate
) {

    @GetMapping("/crawling")
    fun crawling(
        @ParameterObject
        @Valid
        productCrawlingParameter: ProductCrawlingParameter,
    ) : ResponseEntity<ProductCrawlingResponse> {
        val command = ProductCrawlingUseCase.ProductCrawlingCommand(url = productCrawlingParameter.url)
        val store = getStore(productCrawlingParameter.url)

        val productCrawlingUseCase = productCrawlingUseCaseResolver.resolve(store)
        val result = productCrawlingUseCase.crawling(command)

//        val optionUrl = getOptionUrl(result.number, store)
//        val optionResponse = restTemplate.getForEntity(optionUrl, OptionResponse::class.java).body



        val response = ProductCrawlingResponse.from(result)

        return ResponseEntity.ok(response)
    }

    private fun getStore(url: String): Store {
        return when {
            url.contains("musinsa") -> Store.MUSINSA
            url.contains("a-bly") -> Store.A_BLY
            else -> throw IllegalArgumentException("지원하지 않는 URL입니다.")
        }
    }

    private fun getOptionUrl(goodsNo: Long, store: Store): String {
        return when (store) {
            Store.MUSINSA -> "https://goods-detail.musinsa.com/api2/goods/${goodsNo}/options?goodsSaleType=SALE"
            else -> throw NotFoundStoreException()
        }
    }

    data class ProductCrawlingParameter(
        @field:NotBlank(message = "URL은 비워둘 수 없습니다.")
        @field:Pattern(
            regexp = "^(https?://).+",
            message = "URL은 반드시 http 또는 https로 시작해야 합니다."
        )
        val url: String
    )

    data class ProductCrawlingResponse(
        val number: Long,
        val name: String,
        val brand: String,
        val thumbnailUrl: String,
        val store: Store,
        val price: Int,
        val firstCategory: String,
        val secondCategory: String?,
        val firstOptions: List<String>,
        val secondOptions: List<String> = emptyList(),
        val thirdOptions: List<String> = emptyList(),
    ) {

        companion object {
            fun from(productCrawlingResult: ProductCrawlingUseCase.CrawlingResult): ProductCrawlingResponse {
                return ProductCrawlingResponse(
                    number = productCrawlingResult.number,
                    name = productCrawlingResult.name,
                    brand = productCrawlingResult.brand,
                    thumbnailUrl = productCrawlingResult.thumbnailUrl,
                    store = productCrawlingResult.store,
                    price = productCrawlingResult.price,
                    firstCategory = productCrawlingResult.firstCategory,
                    secondCategory = productCrawlingResult.secondCategory,
                    firstOptions = emptyList(),
                    secondOptions = emptyList(),
                    thirdOptions = emptyList()
                )
            }
        }
    }

    data class OptionResponse(
        val data: Data
    )

    data class Data(
        val basic: List<Basic>
    )

    data class Basic(
        val name: String,
        val optionValues: List<OptionValue>
    )

    data class OptionValue(
        val name: String
    )
}