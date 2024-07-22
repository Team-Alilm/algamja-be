package org.teamalilm.alilmbe.application.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.Store
import org.teamalilm.alilmbe.application.port.`in`.use_case.ProductCrawlingCommand
import org.teamalilm.alilmbe.application.port.`in`.use_case.ProductCrawlingResult
import org.teamalilm.alilmbe.application.port.`in`.use_case.ProductCrawlingUseCase
import org.teamalilm.alilmbe.application.port.out.ProductDataGateway
import org.teamalilm.alilmbe.application.port.out.ProductDataGatewayRequest
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
class ProductCrawlingService(
    private val productDataGateway: ProductDataGateway,
    private val restClient: RestClient
) : ProductCrawlingUseCase {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun invoke(command: ProductCrawlingCommand): ProductCrawlingResult {
        val decodedUrl = decodeUrl(command.url)
        val productNumber = extractProductNumber(decodedUrl)
        val document = productDataGateway.invoke(ProductDataGatewayRequest(decodedUrl)).document

        val (category, name, price) = parseDescription(document)
        val soldoutCheckResponse = fetchSoldoutCheckResponse(decodedUrl)

        val options = extractOptions(soldoutCheckResponse)

        return ProductCrawlingResult(
            number = productNumber,
            name = name,
            brand = fetchBrand(document),
            imageUrl = fetchImageUrl(document),
            category = category,
            price = price,
            store = Store.MUSINSA,
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

    private fun extractProductNumber(url: String): Long {
        val regex = Regex("goods/(\\d+)")

        return regex.find(url)?.groupValues?.get(1)?.toLongOrNull() ?: throw IllegalArgumentException("상품 번호를 찾을 수 없습니다.")
    }

    private fun parseDescription(descriptionDoc: org.jsoup.nodes.Document): Triple<String, String, Int> {
        val description = descriptionDoc.select("meta[property=og:description]").attr("content").split(" : ")
        val category = description[1].replace(" 브랜드", "")
        val name = description[4].split(" - ")[0]
        val price = description[4].split(" - ")[1].replace(",", "").replace(" ", "").toInt()
        return Triple(category, name, price)
    }

    private fun fetchSoldoutCheckResponse(url: String): SoldoutCheckResponse? {
        val uri = buildSoldoutCheckUri(url)
        log.info("Fetching soldout check response from URI: $uri")

        return restClient.get().uri(uri).retrieve().body()
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

    private fun fetchBrand(descriptionDoc: org.jsoup.nodes.Document): String {
        return descriptionDoc.select("meta[property=product:brand]").attr("content").ifBlank { "없음" }
    }

    private fun fetchImageUrl(descriptionDoc: org.jsoup.nodes.Document): String {
        return descriptionDoc.getElementById("fbOgImage")?.attr("content") ?: "null"
    }

}
