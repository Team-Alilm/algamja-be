package org.teamalilm.alilmbe.service.crawling

import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.teamalilm.alilmbe.domain.product.entity.Product
import org.teamalilm.alilmbe.global.quartz.data.SoldoutCheckResponse
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Service
@Transactional(readOnly = true)
class ProductCrawlingService {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val restClient = RestClient.create()

    // 크롤링을 담당하는 함수
    fun crawling(command: ProductCrawlingCommand): ProductCrawlingResult {
        // https://www.musinsa.com/app/goods/3262292/option/3262292_001?goodsSaleType=SALE
        val decodedUrl = URLDecoder.decode(command.url, StandardCharsets.UTF_8.toString())
        val url = URI.create(decodedUrl).toString()
        log.info("url: $url")

        val regex = Regex("goods/(\\d+)")
        val matchResult = regex.find(url)
        val number = matchResult?.groupValues?.get(1)?.toLong()
        log.info("number: $number")

        val descriptionDoc = Jsoup
            .connect(url)
            .get()

        val description =
            descriptionDoc.select("meta[property=og:description]").attr("content").split(" : ")
        val category = description[1].replace(" 브랜드", "")
        val name = description[4].split(" - ")[0]
        val price = description[4]
            .split(" - ")[1]
            .replace(",", "")
            .replace(" ", "")
            .toInt()

        val uri = url
            .replace("www", "goods-detail")
            .replace("/app", "") + "/options?goodsSaleType=SALE"
        log.info("uri: $uri")

        val soldoutCheckResponse = restClient
            .get()
            .uri(uri)
            .retrieve()
            .body<SoldoutCheckResponse>()
        log.info("soldoutCheckResponse: $soldoutCheckResponse")

        val option1s = soldoutCheckResponse?.data?.basic?.map { it.name } ?: emptyList()
        log.info("option1s: $option1s")

        val option2s = if (soldoutCheckResponse?.data?.basic?.isNotEmpty() == true) {
            soldoutCheckResponse.data.basic[0].subOptions.map { it.name }
        } else {
            emptyList()
        }
        log.info("option2s: $option2s")

        val option3s = if (
                soldoutCheckResponse?.data?.basic?.isNotEmpty() == true &&
                soldoutCheckResponse.data.basic[0].subOptions.isNotEmpty()
            ) {
            soldoutCheckResponse.data.basic[0].subOptions[0].subOptions.map { it.name }
        } else {
            emptyList()
        }
        log.info("option3s: $option3s")


        return ProductCrawlingResult(
            number = number,
            name = name,
            brand = descriptionDoc.select("meta[property=product:brand]").attr("content")
                ?: "내용 없음",
            imageUrl = descriptionDoc.getElementById("fbOgImage")?.attr("content") ?: "null",
            category = category,
            price = price,
            store = Product.ProductInfo.Store.MUSINSA,
            option1List = option1s,
            option2List = option2s,
            option3List = option3s
        )

    }

    data class ProductCrawlingCommand(
        val url: String
    )

    data class ProductCrawlingResult(
        val number: Number?,
        val name: String,
        val brand: String,
        val imageUrl: String,
        val category: String,
        val price: Int,
        val store: Product.ProductInfo.Store,
        val option1List: List<String>,
        val option2List: List<String>,
        val option3List: List<String>
    )

}