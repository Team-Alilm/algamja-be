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

        // GraphQL 쿼리
        val query = loadGraphQLQuery("zigzag.graphql")
        log.info("Query: $query")

        val variables = mapOf("catalog_product_id" to product.number)

        val requestBody = mapOf(
            "query" to query,
            "variables" to variables
        )

        val response = restClient.post()
            .uri(responseUrl)
            .header("Content-Type", "application/json")
            .body(requestBody)
            .retrieve()
            .body(JsonNode::class.java)

        val itemList = response?.get("data")?.get("pdp_option_info")?.get("catalog_product")?.get("item_list") ?: return true

        itemList.first {
            it.get("name")?.asText() == product.getZigzagOptionName()
        }.get("sales_status")?.asText()?.let {
            return it == "SOLD_OUT"
        } ?: return true

    }

    private fun loadGraphQLQuery(filename: String): String {
        val classLoader = Thread.currentThread().contextClassLoader
        val inputStream = classLoader.getResourceAsStream("graphql/$filename")
            ?: throw IllegalArgumentException("File not found: graphql/$filename")

        return inputStream.bufferedReader().use { it.readText() }
    }

}