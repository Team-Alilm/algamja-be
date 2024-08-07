package org.teamalilm.alilmbe.application.service

import com.google.gson.JsonParser
import org.aspectj.util.LangUtil.extractOptions
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.teamalilm.alilmbe.application.port.`in`.use_case.CrawlingUseCase
import org.teamalilm.alilmbe.application.port.out.gateway.CrawlingGateway
import org.teamalilm.alilmbe.application.port.out.gateway.CrawlingGateway.CrawlingGatewayRequest
import org.teamalilm.alilmbe.domain.Product
import org.teamalilm.alilmbe.global.quartz.data.SoldoutCheckResponse
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * 상품 크롤링 서비스
 * 현재 무신사 종속적인 코드로 구현되어 있음
 */
@Service
@Transactional(readOnly = true)
class CrawlingService(
    private val crawlingGateway: CrawlingGateway,
    private val restClient: RestClient
) : CrawlingUseCase {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun productCrawling(command: CrawlingUseCase.ProductCrawlingCommand): CrawlingUseCase.CrawlingResult {
        val decodedUrl = decodeUrl(command.url)
        val document = crawlingGateway.crawling(CrawlingGatewayRequest(decodedUrl)).document
        val scriptContent = document.getElementsByTag("script").html()
        val jsonData = extractJsonData(scriptContent, "window.__MSS__.product.state")
        val jsonObject = JsonParser.parseString(jsonData).asJsonObject

        val soldoutCheckResponse = fetchSoldoutCheckResponse(decodedUrl)
        val options = extractOptions(soldoutCheckResponse)

        return CrawlingUseCase.CrawlingResult(
            number = jsonObject.get("goodsNo").asLong,
            name = jsonObject.get("goodsNm").asString,
            brand = jsonObject.get("brandNm").asString,
            imageUrl = "https://image.msscdn.net${jsonObject.get("thumbnailImageUrl").asString}",
            category = jsonObject.get("category").asJsonObject.get("categoryDepth1Title").asString,
            price = jsonObject.get("goodsPrice").asJsonObject.get("maxPrice").asInt,
            store = Product.Store.MUSINSA,
            option1List = options.first,
            option2List = options.second,
            option3List = options.third
        )
    }

    private fun decodeUrl(url: String): String {
        return URLDecoder.decode(url, StandardCharsets.UTF_8.toString()).let {
            URI.create(it).toString()
        }
    }

    private fun fetchSoldoutCheckResponse(url: String): SoldoutCheckResponse? {
        val uri = buildSoldoutCheckUri(url)
        log.info("Fetching soldout check response from URI: $uri")

        return restClient.get().uri(uri).retrieve().body<SoldoutCheckResponse>()
    }

    private fun buildSoldoutCheckUri(url: String): String {
        return url.replace("www", "goods-detail").replace("/app", "") + "/options?goodsSaleType=SALE"
    }

    private fun extractOptions(response: SoldoutCheckResponse?): Triple<List<String>, List<String>, List<String>> {
        val option1s = response?.data?.basic?.map { it.name } ?: emptyList()
        val option2s = response?.data?.basic?.firstOrNull()?.subOptions?.map { it.name } ?: emptyList()
        val option3s = response?.data?.basic?.firstOrNull()?.subOptions?.firstOrNull()?.subOptions?.map { it.name } ?: emptyList()
        return Triple(option1s, option2s, option3s)
    }

    private fun extractJsonData(scriptContent: String, variableName: String): String? {
        var jsonString: String? = null

        // 자바스크립트 내 변수 선언 패턴
        val pattern = "$variableName = "

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
