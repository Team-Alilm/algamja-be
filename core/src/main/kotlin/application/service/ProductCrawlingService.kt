package org.team_alilm.application.service

import com.google.gson.JsonObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.ProductCrawlingUseCase
import org.team_alilm.application.port.out.gateway.crawling.CrawlingGateway.*
import org.team_alilm.application.port.out.gateway.crawling.CrawlingGatewayResolver
import org.team_alilm.domain.product.Store
import org.team_alilm.global.error.NotFoundStoreException

@Service
@Transactional(readOnly = true)
class ProductCrawlingService(
    private val crawlingGatewayResolver: CrawlingGatewayResolver
) : ProductCrawlingUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun crawling(command: ProductCrawlingUseCase.ProductCrawlingCommand): ProductCrawlingUseCase.CrawlingResult {
        val store = getStore(command.url)
        val crawlingGateway = crawlingGatewayResolver.resolve(store)

        val crawlingGatewayResponse = crawlingGateway.crawling(
            CrawlingGatewayRequest(
                url = command.url
            )
        )

        val jsonData = extractJsonData(scriptContent = crawlingGatewayResponse.html)

        // jsonData를 파싱하고 싶어
        val jsonObject = JsonObject()
        val product = jsonObject.getAsJsonObject("product")
        

        return ProductCrawlingUseCase.CrawlingResult(
            id = 0,
            number = 0,
            name = "test",
            brand = "test",
            thumbnailUrl = "test",
            firstCategory = "test",
            secondCategory = "test",
            price = 0,
            store = Store.A_BLY,
            firstOption = "test",
            secondOption = null,
            thirdOption = null
        )
    }

    private fun getStore(url: String): Store {
        return when {
            url.contains("musinsa") -> Store.MUSINSA
            url.contains("a-bly") -> Store.A_BLY
            else -> throw NotFoundStoreException()
        }
    }

    private fun extractJsonData(scriptContent: String): String? {
        var jsonString: String? = null

        // 자바스크립트 내 변수 선언 패턴
        val pattern = "window.__MSS__.product.state = "
        // 패턴의 시작 위치 찾기
        val startIndex = scriptContent.indexOf(pattern)

        if (startIndex != -1) {
            // 패턴 이후 부분 추출
            val substring = scriptContent.substring(startIndex + pattern.length)
            // JSON 데이터의 끝 위치 찾기
            val endIndex = substring.indexOf("};") + 1
            // JSON 문자열 추출
            jsonString = substring.substring(0, endIndex)
        }

        return jsonString
    }
}