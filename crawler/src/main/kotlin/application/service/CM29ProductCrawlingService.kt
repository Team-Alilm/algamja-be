package org.team_alilm.application.service

import com.fasterxml.jackson.databind.JsonNode
import domain.product.Store
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.team_alilm.application.port.use_case.ProductCrawlingUseCase
import util.StringContextHolder

@Service
class CM29ProductCrawlingService(
    private val restClient: RestClient
) : ProductCrawlingUseCase {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun crawling(command: ProductCrawlingUseCase.ProductCrawlingCommand): ProductCrawlingUseCase.CrawlingResult {
        val productNumber = command.productNumber
        val productDetailApiUrl = StringContextHolder.CM29_PRODUCT_DETAIL_API_URL.get().format(productNumber)

        val productDetailResponse = restClient.get()
            .uri(productDetailApiUrl)
            .retrieve()
            .body(JsonNode::class.java)

        val productDetailResponseData = productDetailResponse?.get("data") ?: throw IllegalArgumentException()
        log.info("productDetailResponseData: $productDetailResponseData")

        val productCategory = productDetailResponseData.get("frontCategoryInfo")[0]

        return ProductCrawlingUseCase.CrawlingResult(
            number = productNumber,
            name = productDetailResponseData.get("itemName")?.asText() ?: throw IllegalArgumentException(),
            brand = productDetailResponseData.get("frontBrand")?.get("brandNameKor")?.asText() ?: throw IllegalArgumentException(),
            thumbnailUrl = "https://img.29cm.co.kr" + productDetailResponseData.get("itemImages")[0]?.get("imageUrl")?.asText(),
            imageUrlList = productDetailResponseData.get("itemImages")?.drop(1)?.map {
                "https://img.29cm.co.kr" + it.get("imageUrl")?.asText()
            } ?: emptyList(),
            firstCategory = productCategory.get("category1Name")?.asText() ?: throw IllegalArgumentException(),
            secondCategory = productCategory.get("category2Name")?.asText() ?: throw IllegalArgumentException(),
            price = productDetailResponseData.get("consumerPrice")?.asInt() ?: throw IllegalArgumentException(),
            store = Store.CM29,
            firstOptions = productDetailResponseData.get("optionItems")?.get("list")?.map {
                it.get("title")?.asText() ?: throw IllegalArgumentException()
            } ?: emptyList(),
            secondOptions = productDetailResponseData.get("optionItems")?.get("list")?.toList()?.getOrNull(0)
            ?.get("list")?.map {
                it.get("title")?.asText() ?: throw IllegalArgumentException()
            } ?: emptyList(),
            thirdOptions = productDetailResponseData.get("optionItems")?.get("list")?.toList()?.getOrNull(0)
                ?.get("list")?.toList()?.getOrNull(0)
                    ?.get("list")?.map {
                        it.get("title")?.asText() ?: throw IllegalArgumentException()
                    } ?: emptyList(),
        )
    }
}