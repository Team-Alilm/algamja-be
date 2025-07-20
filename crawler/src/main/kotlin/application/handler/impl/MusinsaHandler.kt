package org.team_alilm.application.handler.impl

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.*
import domain.product.Product
import org.team_alilm.gateway.CrawlingGateway
import org.team_alilm.application.handler.PlatformHandler
import org.team_alilm.gateway.SendSlackGateway
import util.StringContextHolder

@Component
class MusinsaHandler(
    private val objectMapper: ObjectMapper,
    private val restClient: RestClient,
    private val crawlingGateway: CrawlingGateway,
    private val sendSlackGateway: SendSlackGateway
) : PlatformHandler {

    private val goodsSaleTypeKey = "goodsSaleType"
    private val saleValue = "\"SOLDOUT\""

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun process(product: Product): Boolean {
        return !isSoldOut(product)
    }

    private fun isSoldOut(product: Product): Boolean {
        val productHtmlUrl = StringContextHolder.MUSINSA_PRODUCT_HTML_URL.get().format(product.storeNumber)
        val crawlingResponse = crawlProductHtml(productHtmlUrl)
        val jsonData = extractJsonFromHtml(crawlingResponse) ?: ""

        log.info("Crawled HTML for product[{}]: {}", product.id, crawlingResponse)
        return isProductAvailable(jsonData) || checkSoldOutViaApi(product)
    }

    private fun crawlProductHtml(url: String): String {
        val request = CrawlingGateway.CrawlingGatewayRequest(url)
        return crawlingGateway.htmlCrawling(request).document.html()
    }

    private fun extractJsonFromHtml(html: String): String? {
        val pattern = "window.__MSS__.product.state = "
        val startIndex = html.indexOf(pattern)
        if (startIndex == -1) return null

        val jsonStart = html.substring(startIndex + pattern.length)
        var braceCount = 0
        var isInString = false
        var escapeNext = false
        var endIndex = -1

        for ((i, c) in jsonStart.withIndex()) {
            if (escapeNext) {
                escapeNext = false
                continue
            }

            when (c) {
                '\\' -> escapeNext = true
                '"' -> isInString = !isInString
                '{' -> if (!isInString) braceCount++
                '}' -> if (!isInString) {
                    braceCount--
                    if (braceCount == 0) {
                        endIndex = i
                        break
                    }
                }
            }
        }

        return if (endIndex != -1) jsonStart.substring(0, endIndex + 1) else null
    }

    private fun isProductAvailable(jsonData: String): Boolean {
        val jsonObject = objectMapper.readTree(jsonData)
        val saleType = jsonObject[goodsSaleTypeKey]?.toString()

        log.info("Sale type from JSON: $saleType")

        return saleType == saleValue
    }

    private fun checkSoldOutViaApi(product: Product): Boolean {
        val apiUrl = StringContextHolder.MUSINSA_OPTION_API_URL.get().format(product.storeNumber)
        return try {
            val response = restClient.get()
                .uri(apiUrl)
                .retrieve()
                .body(JsonNode::class.java)

            val optionItems = response?.get("data")?.get("optionItems")

            log.info("Response from Musinsa API optionItems for product[{}] : {}", product.id, optionItems)

            val optionItem = optionItems?.firstOrNull {
                log.info("Option item for product[{}] : {}", product.id, it)
                it["managedCode"]?.asText() == product.getManagedCode()
            }

            log.info("Option item for product[{}] : {}", product.id, optionItem)

            return (optionItem?.get("isSoldOut"))?.asBoolean() ?: true
        } catch (e: Exception) {
            log.error("❌ Failed to fetch product detail from Musinsa product[{}]", product.id, e)
            sendSlackGateway.sendMessage("❌ Failed to fetch product detail from Musinsa product[${product.id}]")
            true
        }
    }
}
