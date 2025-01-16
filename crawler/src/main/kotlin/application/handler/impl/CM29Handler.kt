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
        val productDetailJsonNode = getProductDetailJsonNode(product.number) ?: return true
        val optionItems = productDetailJsonNode.get("optionItems")

        log.info("Option items: $optionItems")

        if (optionItems == null || optionItems.size() == 0) {
            log.info("Option items are null or empty")
            return productDetailJsonNode.get("itemStockStatus").asText() == "5"
        }

        val firstOption = optionItems.get("list")?.firstOrNull {
            val title = it.get("title").asText()
            title == (product.firstOption ?: "")
        }

        log.info("First option: $firstOption")

        if (firstOption == null) {
            log.warn("No matching first option found for product: ${product.number}")
            return true
        }

        if (firstOption.get("optionStockStatus")?.asText() == "5" && firstOption.get("list")?.isEmpty == true) {
            log.info("First option is sold out")
            return true
        } else if (firstOption.get("list")?.isEmpty == false) {
            log.info("First option is not sold out")
            val secondOption = firstOption.get("list")?.firstOrNull {
                val title = it.get("title").asText()
                log.info("Second option title: $title")
                title == (product.secondOption ?: "")
            }

            if (secondOption == null) {
                log.warn("No matching second option found for product: ${product.number}")
                return true
            }

            return secondOption.get("optionStockStatus")?.asText() == "5"
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