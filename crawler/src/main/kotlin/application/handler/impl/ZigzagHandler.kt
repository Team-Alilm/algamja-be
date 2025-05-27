package org.team_alilm.application.handler.impl

import com.fasterxml.jackson.databind.JsonNode
import domain.product.Product
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.team_alilm.application.handler.PlatformHandler
import util.StringContextHolder

@Component
class ZigzagHandler(
    private val restClient: RestClient
) : PlatformHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun process(product: Product): Boolean {
        return !isSoldOut(product)
    }

    private fun isSoldOut(product: Product): Boolean {
        val responseUrl = StringContextHolder.ZIGZAG_PRODUCT_SOLD_API_URL.get()
        val query = loadGraphQLQuery()

        val requestBody = mapOf(
            "query" to query,
            "variables" to mapOf("catalog_product_id" to product.storeNumber)
        )

        return try {
            val response = restClient.post()
                .uri(responseUrl)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .retrieve()
                .body(JsonNode::class.java)

            val itemList = response?.path("data")
                ?.path("pdp_option_info")
                ?.path("catalog_product")
                ?.path("item_list")

            if (itemList == null || !itemList.isArray) return true

            itemList.none {
                val name = it.path("name").asText(null)
                val status = it.path("sales_status").asText(null)
                name == product.getZigzagOptionName() && status != "SOLD_OUT"
            }
        } catch (e: Exception) {
            log.error("Zigzag API 호출 실패: ${product.storeNumber}", e)
            true
        }
    }

    private fun loadGraphQLQuery(): String {
        return Thread.currentThread()
            .contextClassLoader
            .getResourceAsStream("graphql/zigzag.graphql")
            ?.bufferedReader()
            ?.use { it.readText() }
            ?: throw IllegalArgumentException("File not found: graphql/zigzag.graphql")
    }
}