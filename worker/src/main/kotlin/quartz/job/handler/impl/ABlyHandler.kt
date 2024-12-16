package org.team_alilm.quartz.job.handler.impl

import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.team_alilm.application.port.out.gateway.crawling.CrawlingGateway
import org.team_alilm.domain.product.Product
import org.team_alilm.global.util.StringConstant
import org.team_alilm.quartz.job.handler.PlatformHandler


@Component
class ABlyHandler : PlatformHandler {

    override fun process(product: Product) {
        if(checkSoldOut(product).not()) {
            sendNotifications(product)
        }
    }

    private fun checkSoldOut(product: Product): Boolean {
        val musinsaProductHtmlRequestUrl = StringConstant.MUSINSA_PRODUCT_HTML_URL.get().format(product.number)
        val crawlingGatewayRequest = CrawlingGateway.CrawlingGatewayRequest(musinsaProductHtmlRequestUrl)
        val response = crawlingGateway.htmlCrawling(crawlingGatewayRequest)
        val jsonData = extractJsonData(response.html)

        return if (jsonData != null) {
            // JSON 데이터 파싱
            val jsonObject = objectMapper.readTree(jsonData)
            val isGoodsSaleTypeEqualsSALE = jsonObject.get("goodsSaleType").toString() == "\"SALE\""

            if (isGoodsSaleTypeEqualsSALE.not()) {
                true
            } else {
                // API 호출로 재확인
                val requestUri = StringConstant.MUSINSA_OPTION_API_URL.get().format(product.number)
                try {
                    checkIfSoldOut(requestUri, product)
                } catch (e: RestClientException) {
                    log.error("Failed to check soldout status of product: ${product.number}", e)
                    sendSlackGateway.sendMessage("무신사 서버에 요청 시 에러가 발생했어요.: ${product.number}\nError: ${e.message}")
                    true
                }
            }
        } else {
            log.error("No JSON data found for product: ${product.number}")
            val requestUri = StringConstant.MUSINSA_OPTION_API_URL.get().format(product.number)

            try {
                checkIfSoldOut(requestUri, product)
            } catch (e: RestClientException) {
                log.error("Failed to check soldout status of product: ${product.number}", e)
                sendSlackGateway.sendMessage("무신사 서버에 요청 시 에러가 발생했어요.: ${product.number}\nError: ${e.message}")
                true
            }
        }
    }
}