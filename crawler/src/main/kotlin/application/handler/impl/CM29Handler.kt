package org.team_alilm.application.handler.impl

import com.fasterxml.jackson.databind.JsonNode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import domain.product.Product
import org.team_alilm.application.handler.PlatformHandler
import util.StringContextHolder

@Component
class CM29Handler(
    private val restClient: RestClient
) : PlatformHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun process(product: Product): Boolean {
        return !isSoldOut(product)
    }

    private fun isSoldOut(product: Product): Boolean {
        val productDetailJsonNode = getProductDetailJsonNode(product.storeNumber) ?: return true
        val optionItems = productDetailJsonNode["optionItems"]

        if (optionItems == null || optionItems["list"] == null || optionItems["list"].size() == 0) {
            return productDetailJsonNode["itemStockStatus"]?.asText() == "5"
        }

        val firstOption = optionItems["list"].firstOrNull {
            val title = it["title"]?.asText().orEmpty()
            title == (product.firstOption ?: "")
        } ?: return true

        val firstOptionList = firstOption["list"]

        if (firstOption["optionStockStatus"]?.asText() == "5" && (firstOptionList == null || firstOptionList.size() == 0)) {
            return true
        }

        if (firstOptionList != null && firstOptionList.size() > 0) {
            val secondOption = firstOptionList.firstOrNull {
                val title = it["title"]?.asText().orEmpty()
                log.info("Second option title: $title")
                title == (product.secondOption ?: "")
            } ?: return true

            return secondOption["optionStockStatus"]?.asText() == "5"
        }

        return false
    }

    private fun getProductDetailJsonNode(productNumber: Long): JsonNode? {
        try {
            log.info("Fetching product detail for productNumber: $productNumber")
            val response = restClient.get()
                .uri(StringContextHolder.CM29_PRODUCT_DETAIL_API_URL.get().format(productNumber))
                .retrieve()
                .body(JsonNode::class.java)
            log.info("Response received: $response")
            return response?.get("data")
        } catch (e: Exception) {
            log.error("Failed to fetch product detail for productNumber: $productNumber", e)
            return null
        }
    }

}