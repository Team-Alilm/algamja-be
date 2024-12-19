package org.team_alilm.quartz.job.handler.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.*
import org.team_alilm.application.service.NotificationService
import org.team_alilm.application.port.out.gateway.crawling.CrawlingGateway
import org.team_alilm.domain.product.Product
import org.team_alilm.global.util.StringConstant
import org.team_alilm.quartz.data.SoldoutCheckResponse
import org.team_alilm.quartz.job.SoldoutCheckJob
import org.team_alilm.quartz.job.handler.PlatformHandler

@Component
class MusinsaHandler(
    private val objectMapper: ObjectMapper,
    private val restTemplate: RestTemplate,
    private val crawlingGateway: CrawlingGateway,
    private val notificationService: NotificationService
) : PlatformHandler {

    private val log = LoggerFactory.getLogger(SoldoutCheckJob::class.java)
    private val goodsSaleTypeKey = "goodsSaleType"
    private val saleValue = "\"SALE\""

    override fun process(product: Product) {
        if (!isSoldOut(product)) {
            notificationService.sendNotifications(product)
        }
    }

    private fun isSoldOut(product: Product): Boolean {
        val productHtmlUrl = StringConstant.MUSINSA_PRODUCT_HTML_URL.get().format(product.number)
        val crawlingResponse = crawlProductHtml(productHtmlUrl)
        val jsonData = extractJsonFromHtml(crawlingResponse)

        return if (jsonData != null) {
            isProductAvailable(jsonData) && !checkSoldOutViaApi(product)
        } else {
            log.warn("No JSON data found for product: ${product.number}")
            checkSoldOutViaApi(product)
        }
    }

    private fun crawlProductHtml(url: String): String {
        val request = CrawlingGateway.CrawlingGatewayRequest(url)
        return crawlingGateway.htmlCrawling(request).html
    }

    private fun extractJsonFromHtml(html: String): String? {
        val pattern = "window.__MSS__.product.state = "
        val startIndex = html.indexOf(pattern)
        if (startIndex == -1) return null

        val jsonStart = html.substring(startIndex + pattern.length)
        val endIndex = jsonStart.indexOf("};") + 1
        return if (endIndex > 0) jsonStart.substring(0, endIndex) else null
    }

    private fun isProductAvailable(jsonData: String): Boolean {
        val jsonObject = objectMapper.readTree(jsonData)
        val saleType = jsonObject[goodsSaleTypeKey]?.toString()
        return saleType == saleValue
    }

    private fun checkSoldOutViaApi(product: Product): Boolean {
        val apiUrl = StringConstant.MUSINSA_OPTION_API_URL.get().format(product.number)
        return try {
            val response = restTemplate.getForEntity(apiUrl, SoldoutCheckResponse::class.java).body
            val optionItem = response?.data?.optionItems?.firstOrNull {
                it.managedCode == product.getManagedCode()
            }
            optionItem?.outOfStock ?: true
        } catch (e: RestClientException) {
            handleApiException(e, product)
            true
        }
    }

    private fun handleApiException(e: RestClientException, product: Product) {
        log.error("Failed to check soldout status of product: ${product.number}", e)
        notificationService.notifySlackError(product, e.message)
    }
}
