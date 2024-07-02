package org.teamalilm.alilmbe.service

import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.teamalilm.alilmbe.domain.product.entity.Product

@Service
class ProductCrawlingService {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun crawling(command: ProductCrawlingCommand) : ProductCrawlingResult {
        val doc = Jsoup
            .connect(command.url)
            .get()

        log.info("doc.html(): ${doc.html()}")

//        return ProductCrawlingResult(
//            name = "name",
//            brand = doc.select("meta[property=product:brand]")?.attr("content")? ?: "내용 없음",
//            imageUrl = doc.getElementById("fbOgImage")?.attr("content") ?: "null",
//            price = 0,
//            store = Product.ProductInfo.Store.MUSINSA,
//            option1 = "",
//            option2 = "",
//            option3 = ""
//        )
    }

    data class ProductCrawlingCommand (
        val url: String
    )

    data class ProductCrawlingResult (
        val name: String,
        val brand: String,
        val imageUrl: String,
        val price: Int,
        val store: Product.ProductInfo.Store,
        val option1: String,
        val option2: String,
        val option3: String
    )
}