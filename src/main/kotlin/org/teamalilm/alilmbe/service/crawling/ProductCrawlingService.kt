package org.teamalilm.alilmbe.service.crawling

import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.teamalilm.alilmbe.domain.product.entity.Product
import org.teamalilm.alilmbe.global.quartz.data.SoldoutCheckResponse

@Service
class ProductCrawlingService {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val restClient = RestClient.create()

    fun crawling(command: ProductCrawlingCommand): ProductCrawlingResult {
        val descriptionDoc = Jsoup
            .connect(command.url)
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

        val optionJsonString = restClient
            .get()
            .uri(
                command.url
                    .replace("www", "goods-detail")
                    .replace("/app", "")
            )
            .retrieve()
            .body<SoldoutCheckResponse>()

        val isSoldOut = optionJsonString?.data
        log.info("isSoldOut: $isSoldOut")

        return ProductCrawlingResult(
            name = name,
            brand = descriptionDoc.select("meta[property=product:brand]").attr("content")
                ?: "내용 없음",
            imageUrl = descriptionDoc.getElementById("fbOgImage")?.attr("content") ?: "null",
            category = category,
            price = price,
            store = Product.ProductInfo.Store.MUSINSA,
            option1 = "",
            option2 = "",
            option3 = ""
        )

    }

    data class ProductCrawlingCommand(
        val url: String
    )

    data class ProductCrawlingResult(
        val name: String,
        val brand: String,
        val imageUrl: String,
        val category: String,
        val price: Int,
        val store: Product.ProductInfo.Store,
        val option1: String,
        val option2: String,
        val option3: String
    )

}