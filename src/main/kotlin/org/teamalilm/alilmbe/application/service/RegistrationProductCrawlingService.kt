package org.teamalilm.alilmbe.application.service

import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.ProductJpaEntity
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.Store
import org.teamalilm.alilmbe.adapter.out.persistence.repository.SpringDataProductRepository
import org.teamalilm.alilmbe.application.port.`in`.use_case.ProductCrawlingCommand
import org.teamalilm.alilmbe.application.port.`in`.use_case.ProductCrawlingResult
import org.teamalilm.alilmbe.application.port.`in`.use_case.ProductCrawlingUseCase
import org.teamalilm.alilmbe.global.quartz.data.SoldoutCheckResponse
import java.net.URI.*
import java.net.URLDecoder.*
import java.nio.charset.StandardCharsets

@Service
@Transactional(readOnly = true)
class ProductCrawlingService (
    private val springDataProductRepository: SpringDataProductRepository,
    private val restClient: RestClient
) : ProductCrawlingUseCase {

    private val log = org.slf4j.LoggerFactory.getLogger(this::class.java)

    // 크롤링을 담당하는 함수
    override fun invoke(command: ProductCrawlingCommand): ProductCrawlingResult {
        // url 디코딩
        // https://www.musinsa.com/app/goods/3262292/option/3262292_001?goodsSaleType=SALE
        val decodedUrl = decode(command.url, StandardCharsets.UTF_8.toString())
        val url = create(decodedUrl).toString()
        log.info("url: $url")

        // 상품 번호 추출
        val regex = Regex("goods/(\\d+)")
        val matchResult = regex.find(url)
        val number = matchResult?.groupValues?.get(1)?.toLong()
        log.info("number: $number")

        val storeName = Regex("www.(.*?).com").find(url)?.groupValues?.get(1) ?: ""

        // 상품 정보 크롤링
        val doc = Jsoup
            .connect(url)
            .get()

        // 상품 정보 추출
        val description = doc
            .select("meta[property=og:description]")
            .attr("content")
            .split(" : ")

        val name = description[4]
            .split(" - ")[0]
        log.info("name: $name")

        val category = description[1]
            .replace(" 브랜드", "")
        log.info("category: $category")

        val imageUrl = doc.getElementById("fbOgImage")?.attr("content") ?: ""

        val price = description[4]
            .split(" - ")[1]
            .replace(",", "")
            .replace(" ", "")
            .toInt()
        log.info("price: $price")

        val productOptionAPIRequestUri = url
            .replace("www", "goods-detail")
            .replace("/app", "") + "/options?goodsSaleType=SALE"
        log.info("productOptionAPIRequestUri: $productOptionAPIRequestUri")

        val soldoutCheckResponse = restClient
            .get()
            .uri(productOptionAPIRequestUri)
            .retrieve()
            .body<SoldoutCheckResponse>()
        log.info("soldoutCheckResponse: $soldoutCheckResponse")

        val option1List = soldoutCheckResponse?.data?.basic?.map { it.name } ?: emptyList()
        log.info("option1s: $option1List")

        val option2List = if (soldoutCheckResponse?.data?.basic?.isNotEmpty() == true) {
            soldoutCheckResponse.data.basic[0].subOptions.map { it.name }
        } else {
            emptyList()
        }
        log.info("option2s: $option2List")

        val option3List = if (
            soldoutCheckResponse?.data?.basic?.isNotEmpty() == true &&
            soldoutCheckResponse.data.basic[0].subOptions.isNotEmpty()
        ) {
            soldoutCheckResponse.data.basic[0].subOptions[0].subOptions.map { it.name }
        } else {
            emptyList()
        }
        log.info("option3s: $option3List")

        val productJpaEntity = ProductJpaEntity(
            name = name,
            category = category,
            price = price,
            imageUrl = imageUrl,
            store = Store.fromString(storeName),
            option1List = option1List,
            option2List = option2List,
            option3List = option3List

        )

        return ProductCrawlingResult(

        )
    }

}