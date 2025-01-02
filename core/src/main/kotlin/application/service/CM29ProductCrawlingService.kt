package org.team_alilm.application.service

import com.fasterxml.jackson.databind.JsonNode
import domain.product.Store
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import org.team_alilm.application.port.`in`.use_case.product.crawling.ProductCrawlingUseCase
import org.team_alilm.global.error.NotParserProduct
import util.StringContextHolder

@Service
@Transactional
class CM29ProductCrawlingService(
    private val restClient: RestClient
) : ProductCrawlingUseCase {

    override fun crawling(command: ProductCrawlingUseCase.ProductCrawlingCommand): ProductCrawlingUseCase.CrawlingResult {
        val productNumber = command.productNumber
        val productDetailApiUrl = StringContextHolder.CM29_PRODUCT_DETAIL_API_URL.get().format(productNumber)

        val productDetailResponse = restClient.get()
            .uri(productDetailApiUrl)
            .retrieve()
            .body(JsonNode::class.java)

        val productDetailResponseData = productDetailResponse?.get("data") ?: throw NotParserProduct()
        val productCategorys = productDetailResponseData.get("frontCategoryInfo").firstOrNull {
            it.get("category2Name")?.asText()?.matches(Regex("^[가-힣]+$")) ?: false
                    && it.get("category3Name")?.asText()?.matches(Regex("^[가-힣]+$")) ?: false
        } ?: throw NotParserProduct()

        return ProductCrawlingUseCase.CrawlingResult(
            number = productNumber,
            name = productDetailResponseData.get("itemName")?.asText() ?: throw NotParserProduct(),
            brand = productDetailResponseData.get("frontBrand")?.get("brandNameKor")?.asText() ?: throw NotParserProduct(),
            thumbnailUrl = "https://img.29cm.co.kr" + productDetailResponseData.get("itemImages")[0]?.get("imageUrl")?.asText(),
            imageUrlList = productDetailResponseData.get("itemImages")?.drop(1)?.map {
                "https://img.29cm.co.kr" + it.get("imageUrl")?.asText()
            } ?: emptyList(),
            firstCategory = productCategorys.get("category2Name")?.asText() ?: throw NotParserProduct(),
            secondCategory = productCategorys.get("category3Name")?.asText() ?: throw NotParserProduct(),
            price = productDetailResponseData.get("consumerPrice")?.asInt() ?: throw NotParserProduct(),
            store = Store.CM29,
            firstOptions = productDetailResponseData.get("optionItems")?.get("list")?.map {
                it.get("title")?.asText() ?: throw NotParserProduct()
            } ?: emptyList(),
            secondOptions = productDetailResponseData.get("optionItems")?.get("list")?.toList()?.getOrNull(0)
            ?.get("list")?.map {
                it.get("title")?.asText() ?: throw NotParserProduct()
            } ?: emptyList(),
            thirdOptions = productDetailResponseData.get("optionItems")?.get("list")?.toList()?.getOrNull(0)
                ?.get("list")?.toList()?.getOrNull(0)
                    ?.get("list")?.map {
                        it.get("title")?.asText() ?: throw NotParserProduct()
                    } ?: emptyList(),
        )
    }
}